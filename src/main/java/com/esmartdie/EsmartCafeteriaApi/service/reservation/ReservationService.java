package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.*;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService implements IReservationService{

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IUserRepository userRepository;


    @Override
    public ReservationDTO createReservation(NewReservationDTO reservationDTO) {

        Reservation reservation = createReservationFromDTO(reservationDTO);

        isReservationPossible(reservation);

        int dinners = reservation.getDinners();

        if(dinners < 1 || dinners > 6){
            throw new ReservationException("Reservation is not possible.");
        }

        reservation.setReservationStatus(ReservationStatus.ACCEPTED);
        Reservation savedReservation = reservationRepository.save(reservation);
        recalculateTotalDinners(reservation.getReservationDate(), reservation.getShift());


        return convertToReservationDTO(savedReservation);
    }

    private Reservation createReservationFromDTO (NewReservationDTO reservationDTO){

        Client client = (Client)userRepository.findById(reservationDTO.getClient().getId()).orElseThrow(
                ()->new ResourceNotFoundException("Client not found with id: " +reservationDTO.getClient().getId()));

        ReservationRecord record = reservationRecordRepository.
                findByReservationDateAndShift(reservationDTO.getReservationDate(), reservationDTO.getShift()).orElseThrow(
                        ()->new IllegalCalendarException("Calendar not found with with date " +
                                reservationDTO.getReservationDate()+ " and shift "+ reservationDTO.getShift()));


        return new Reservation(
                client,
                reservationDTO.getDinners(),
                record,
                reservationDTO.getReservationDate(),
                reservationDTO.getShift());
    }

    private void isReservationPossible(Reservation reservation){
        if (!checkEmptySpace(reservation)) {
            throw new ReservationException("Reservation is not possible due to lack of available spaces.");
        } else if (checkPassReserved(reservation)) {
            throw new ReservationException("Couldn't made a reservation on a passed day");
        }else if(hasOtherAcceptedReservation(reservation)){
            throw new ReservationException("Clients couldn't had different reservation for the same day and shift");
        }
    }

    private boolean checkEmptySpace(Reservation reservation) {
        LocalDate reservationDate = reservation.getReservationDate();
        Shift shift = reservation.getShift();
        Optional<ReservationRecord> optionalReservationRecord =
                reservationRecordRepository.findByReservationDateAndShift(reservationDate, shift);
        if (optionalReservationRecord.isPresent()) {
            ReservationRecord reservationRecord = optionalReservationRecord.get();
            return reservationRecord.getEmptySpaces() >= reservation.getDinners();
        }
        return true;
    }

    private boolean checkPassReserved(Reservation reservation){
        LocalDate reservationDate = reservation.getReservationDate();
        LocalDate today = LocalDate.now();
        if(reservationDate.isBefore(today)) {
            return true;
        }
        return false;
    }

    private boolean hasOtherAcceptedReservation(Reservation reservation){
        Client client = reservation.getClient();
        Shift shift = reservation.getShift();
        LocalDate reservationDate = reservation.getReservationDate();

        Optional<Reservation> optionalReservation =
                reservationRepository.findByClientAndReservationDateAndShift(client, reservationDate, shift)
                        .stream()
                        .filter(r -> r.getReservationStatus() == ReservationStatus.ACCEPTED && !r.equals(reservation))
                        .findFirst();

        return optionalReservation.isPresent();


    }

    private void recalculateTotalDinners(LocalDate reservationDate, Shift shift) {

        ReservationRecord reservationRecord = reservationRecordRepository
                .findByReservationDateAndShift(reservationDate, shift)
                .orElseThrow(() -> new IllegalCalendarException("No reservations found for the specified date and shift"));
        List<Reservation> reservations = reservationRepository.findAllByReservationDateAndShift(reservationDate, shift);

        int totalDinners = reservations.stream()
                .filter(reservation -> reservation.getReservationStatus() == ReservationStatus.ACCEPTED)
                .mapToInt(Reservation::getDinners)
                .sum();

        int emptySpaces = reservationRecord.getMAX_CLIENTS() - totalDinners;
        reservationRecord.setEmptySpaces(emptySpaces);

        reservationRecordRepository.save(reservationRecord);
    }

    @Override
    public List<ReservationDTO> getReservationsByClient(Client client) {
        List <Reservation> reservations = reservationRepository.findAllByClient(client);
        return reservations.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO convertToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDinners(reservation.getDinners());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setShift(reservation.getShift());
        dto.setReservationStatus(reservation.getReservationStatus());


        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName(reservation.getClient().getName());
        clientDTO.setLastName(reservation.getClient().getLastName());
        clientDTO.setEmail(reservation.getClient().getEmail());
        clientDTO.setActive(reservation.getClient().getActive());
        clientDTO.setRating(reservation.getClient().getRating());

        dto.setClientDTO(clientDTO);
        return dto;
    }

    @Override
    public Client getClientFromAuthentication(Authentication authentication){

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomAuthenticationException("User not authenticated.");
        }
        Object principal = authentication.getPrincipal();

        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new CustomAuthenticationException("The authentication principal is not recognized.");
        }

        if (email == null) {
            throw new CustomAuthenticationException ("User email is not available.");
        }

        Optional<User> clientOptional = userRepository.findByEmail(email);
        return (Client)clientOptional.orElseThrow(() -> new ClientNotFoundException("Client not found with email: " + email));
    }


    @Override
    public List<ReservationDTO> getAcceptedReservationsByClient(Client client) {
        List <Reservation> reservations = reservationRepository.findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
        return reservations.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO getReservationById(Long id) throws ReservationNotFoundException {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));

        return convertToReservationDTO(reservation);
    }

    @Override
    public List<ReservationDTO> getAllReservationsForDay(LocalDate date) {

        List <Reservation> reservations =reservationRepository.findAllByReservationDate(date);

        return reservations.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getAllReservationsForDayAndShift(LocalDate date, Shift shift) {

        List <Reservation> reservations = reservationRepository.findAllByReservationDateAndShift(date, shift);

        return reservations.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId, Client client) throws ReservationNotFoundException, ReservationException {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getClient().getId().equals(client.getId())) {
            throw new ReservationException("You are not authorized to cancel this reservation.");
        }

        if (reservation.getReservationStatus() != ReservationStatus.ACCEPTED) {
            throw new ReservationException("This reservation can't be canceled as it is not in an acceptable state.");
        }

        LocalDate today = LocalDate.now();
        if (reservation.getReservationDate().isEqual(today)) {
            throw new ReservationException("Cannot cancel a reservation on the same day.");
        }
        if (reservation.getReservationDate().isBefore(today)) {
            throw new ReservationException("Cannot cancel past reservations.");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELED);
        reservation = reservationRepository.save(reservation);
        recalculateTotalDinners(reservation.getReservationDate(), reservation.getShift());
        return convertToReservationDTO(reservation);
    }

    @Override
    public ReservationDTO updateReservationStatus(Long reservationId, ReservationStatusUpdatedDTO reservationDTO) {
        LocalDate today = LocalDate.now();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        if (!reservationDTO.getActionDate().equals(today) || !reservationDTO.getActionDate().equals(reservation.getReservationDate())) {
            throw new IllegalArgumentException("Reservations can only be updated to 'CONFIRMED' on the same day of the reservation.");
        }

        List<Shift> allowedShifts = getAllowedShifts(reservationDTO.getCurrentTime());
        if (!allowedShifts.contains(reservation.getShift())) {
            throw new ReservationException("This reservation cannot be confirmed at the current time.");
        }

        if (!reservation.getReservationStatus().equals(ReservationStatus.ACCEPTED)) {
            throw new ReservationException("Only accepted reservations can be confirmed.");
        }

        if(reservationDTO.getReservationStatus().equals(ReservationStatus.CONFIRMED)){
            reservation.setReservationStatus(reservationDTO.getReservationStatus());
        } else if (reservationDTO.getReservationStatus().equals(ReservationStatus.LOST)) {
            reservation.setReservationStatus(reservationDTO.getReservationStatus());
        }else{
            throw new ReservationException("Reservations could only be updated to CONFIRMED or LOST status.");
        }
        reservationRepository.save(reservation);

        return convertToReservationDTO(reservation);
    }

    @Override
    public List<ReservationDTO>  updateReservationsToLoss(LocalDate actionDate, LocalTime currentTime) {
        LocalDate today = LocalDate.now();

        List<ReservationDTO> reservationDTOList = new ArrayList<>();

        if (!actionDate.equals(today)) {
            throw new IllegalArgumentException("Reservations can only be updated on the same day.");
        }

        LocalTime actualTime = LocalTime.now();

        if(actualTime.isBefore(LocalTime.of(10,00))){
            LocalDate localDateAfter = actionDate.plusDays(1);
            if(localDateAfter.equals(today)||actionDate.equals(today)){
                today=actionDate;
                currentTime = LocalTime.of(23, 59);
            }
        }

        List<Shift> allowedShifts = getAllowedShifts(currentTime);
        for (Shift shift : allowedShifts) {
            List<Reservation> reservations = reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(actionDate, shift, ReservationStatus.ACCEPTED);
            reservations.forEach(reservation -> reservation.setReservationStatus(ReservationStatus.LOST));
            reservationRepository.saveAll(reservations);
            reservations.forEach(reservation->reservationDTOList.add(convertToReservationDTO(reservation)));
        }

        return reservationDTOList;
    }


    private List<Shift> getAllowedShifts(LocalTime currentTime) {
        List<Shift> allowedShifts = new ArrayList<>();
        if (currentTime.isAfter(LocalTime.of(13, 0))) {
            allowedShifts.add(Shift.DAY1);
        }
        if (currentTime.isAfter(LocalTime.of(14, 0))) {
            allowedShifts.add(Shift.DAY2);
        }
        if (currentTime.isAfter(LocalTime.of(15, 0))) {
            allowedShifts.add(Shift.DAY3);
        }
        if (currentTime.isAfter(LocalTime.of(17, 0))) {
            allowedShifts.add(Shift.DAY4);
        }
        if (currentTime.isAfter(LocalTime.of(19, 0))) {
            allowedShifts.add(Shift.NIGHT1);
        }
        if (currentTime.isAfter(LocalTime.of(20, 0))) {
            allowedShifts.add(Shift.NIGHT2);
        }
        if (currentTime.isAfter(LocalTime.of(21, 0))) {
            allowedShifts.add(Shift.NIGHT3);
        }
        if (currentTime.isAfter(LocalTime.of(22, 0))) {
            allowedShifts.add(Shift.NIGHT4);
        }
        return allowedShifts;
    }

}
