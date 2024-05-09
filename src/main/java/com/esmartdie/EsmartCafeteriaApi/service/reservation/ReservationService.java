package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationException;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService implements IReservationService{

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;


    @Override
    public Reservation createReservation(Reservation reservation) {

        isReservationPossible(reservation);

        int dinners = reservation.getDinners();

        if(dinners < 1 || dinners > 6){
            throw new ReservationException("Reservation is not possible.");
        }

        reservation.setReservationStatus(ReservationStatus.ACCEPTED);
        Reservation savedReservation = reservationRepository.save(reservation);
/*
        ReservationRecord reservationRecord = findOrCreateReservationRecord(reservation.getReservationDate(), reservation.getShift());

        if (reservationRecord.getReservationList() == null) {
            reservationRecord.setReservationList(new ArrayList<>());
        }

        reservationRecord.getReservationList().add(savedReservation);
        reservationRecordRepository.save(reservationRecord);

 */
        recalculateTotalDinners(reservation.getReservationDate(), reservation.getShift());

        return savedReservation;
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


/*
    private ReservationRecord findOrCreateReservationRecord(LocalDate reservationDate, Shift shift) {
        Optional<ReservationRecord> optionalReservationRecord = reservationRecordRepository.findByReservationDateAndShift(reservationDate, shift);
        if (optionalReservationRecord.isPresent()) {
            return optionalReservationRecord.get();
        } else {
            ReservationRecord reservationRecord = new ReservationRecord();
            reservationRecord.setShift(shift);
            reservationRecord.setReservationDate(reservationDate);
            reservationRecordRepository.save(reservationRecord);
            return reservationRecord;
        }
    }

 */

    private void recalculateTotalDinners(LocalDate reservationDate, Shift shift) {

        ReservationRecord reservationRecord = reservationRecordRepository
                .findByReservationDateAndShift(reservationDate, shift)
                .orElseThrow(() -> new ReservationNotFoundException("No reservations found for the specified date and shift"));
        List<Reservation> reservations = reservationRecord.getReservationList();

        int totalDinners = reservations.stream()
                .filter(reservation -> reservation.getReservationStatus() == ReservationStatus.ACCEPTED)
                .mapToInt(Reservation::getDinners)
                .sum();

        int emptySpaces = reservationRecord.getMAX_CLIENTS() - totalDinners;
        reservationRecord.setEmptySpaces(emptySpaces);

        reservationRecordRepository.save(reservationRecord);
    }

    @Override
    public Optional<List<Reservation>> getReservationsByClient(Client client) {
        return reservationRepository.findAllByClient(client);
    }

    @Override
    public Optional<List<Reservation>> getAcceptedReservationsByClient(Client client) {
        return reservationRepository.findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Optional<List<Reservation>> getAllReservationsForDay(LocalDate date) {
        return reservationRepository.findAllByReservationDate(date);
    }

    @Override
    public Optional<List<Reservation>>getAllReservationsForDayAndShift(LocalDate date, Shift shift) {
        return reservationRepository.findAllByReservationDateAndShift(date, shift);
    }

    @Override
    public Reservation cancelReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            if(!reservation.getReservationStatus().equals(ReservationStatus.ACCEPTED)){
                throw new ReservationException("This reservation couldn't be canceled");
            }

            LocalDate today = LocalDate.now();
            LocalDate reservationDate = reservation.getReservationDate();
            Shift shift = reservation.getShift();

            if (reservationDate.isEqual(today)) {
                throw new ReservationException("Cannot cancel a reservation on the same day.");
            } else if (reservationDate.isBefore(today)) {
                throw new ReservationException("Couldn't canceled expired reservations.");
            }

            reservation.setReservationStatus(ReservationStatus.CANCELED);
            reservationRepository.save(reservation);
            recalculateTotalDinners(reservationDate,shift);
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
    }

    @Override
    public Reservation confirmReservation(Long reservationId, LocalDate actionDate, LocalTime currentTime) {
        LocalDate today = LocalDate.now();
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isPresent()) {

            Reservation reservation = optionalReservation.get();

            if (actionDate.equals(today)&&actionDate.equals(reservation.getReservationDate())) {
                List<Shift> allowedShifts = getAllowedShifts(currentTime);

                for (Shift shift : allowedShifts) {

                    if(reservation.getShift().equals(shift)){

                        if(!reservation.getReservationStatus().equals(ReservationStatus.ACCEPTED)){
                            throw new ReservationException("This reservation couldn't updated to confirmed");
                        }
                        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
                        return reservationRepository.save(reservation);
                    }

                }
            }else {
                throw new IllegalArgumentException("Reservations can only be updated to 'CONFIRMED' if the action is performed on the same day.");
            }
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
        return null;
    }

    @Override
    public Reservation lostReservation(Long reservationId, LocalDate actionDate, LocalTime currentTime) {
        LocalDate today = LocalDate.now();

        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {

            Reservation reservation = optionalReservation.get();

            if (actionDate.equals(today)&&actionDate.equals(reservation.getReservationDate())) {
                List<Shift> allowedShifts = getAllowedShifts(currentTime);

                for (Shift shift : allowedShifts) {

                    if(reservation.getShift().equals(shift)){

                        if(!reservation.getReservationStatus().equals(ReservationStatus.ACCEPTED)){
                            throw new ReservationException("This reservation couldn't updated to LOST");
                        }
                        reservation.setReservationStatus(ReservationStatus.LOST);
                        return reservationRepository.save(reservation);
                    }

                }
            }else {
                throw new IllegalArgumentException("Reservations can only be updated to 'LOSS' if the action is performed on the same day.");
            }
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
        return null;
    }

    @Override
    public void updateReservationsToLoss(LocalDate actionDate, LocalTime currentTime) {
        LocalDate today = LocalDate.now();
        LocalTime actualTime = LocalTime.now();

        if(actualTime.isBefore(LocalTime.of(10,00))){
            LocalDate localDateAfter = actionDate.plusDays(1);
            if(localDateAfter.equals(today)||actionDate.equals(today)){
                today=actionDate;
                currentTime = LocalTime.of(23, 59);
            }
        };

        if (actionDate.equals(today)) {
            List<Shift> allowedShifts = getAllowedShifts(currentTime);
            for (Shift shift : allowedShifts) {
                Optional<List<Reservation>> optionalReservations =
                        reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(actionDate, shift, ReservationStatus.ACCEPTED);
                if (optionalReservations.isPresent()) {
                    List<Reservation> reservations = optionalReservations.get();
                    for (Reservation reservation : reservations) {
                        reservation.setReservationStatus(ReservationStatus.LOST);
                    }
                    reservationRepository.saveAll(reservations);
                }
            }
        } else {
            throw new IllegalArgumentException("Reservations can only be updated to 'LOSS' if the action is performed on the same day.");
        }
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
