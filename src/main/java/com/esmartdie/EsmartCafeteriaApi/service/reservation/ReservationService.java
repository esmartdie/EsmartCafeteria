package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationException;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationNotFoundException;
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

        if (!isReservationPossible(reservation)) {
            throw new ReservationException("Reservation is not possible due to lack of available spaces.");
        }

        int dinners = reservation.getDinners();

        if(dinners < 1 || dinners > 6){
            throw new ReservationException("Reservation is not possible.");
        }

        reservation.setReservationStatus(ReservationStatus.ACCEPTED);
        Reservation savedReservation = reservationRepository.save(reservation);

        ReservationRecord reservationRecord = findOrCreateReservationRecord(reservation.getReservationDate(), reservation.getShift());

        if (reservationRecord.getReservationList() == null) {
            reservationRecord.setReservationList(new ArrayList<>());
        }

        reservationRecord.getReservationList().add(savedReservation);
        reservationRecordRepository.save(reservationRecord);
        recalculateTotalDinners(reservation.getReservationDate(), reservation.getShift());

        return savedReservation;
    }

    private boolean isReservationPossible(Reservation reservation) {
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

    private void recalculateTotalDinners(LocalDate reservationDate, Shift shift) {
        Optional<ReservationRecord> optionalReservationRecord = reservationRecordRepository.findByReservationDateAndShift(reservationDate, shift);
        ReservationRecord reservationRecord = optionalReservationRecord.orElseGet(() -> findOrCreateReservationRecord(reservationDate, shift));
        List<Reservation> reservations = reservationRecord.getReservationList();

        if (reservations == null|| reservations.isEmpty()) {
            reservationRecord.setEmptySpaces(reservationRecord.getMAX_CLIENTS());
        }else {

            int totalDinners = reservations.stream()
                    .filter(reservation -> reservation.getReservationStatus() == ReservationStatus.ACCEPTED)
                    .mapToInt(Reservation::getDinners)
                    .sum();

            int emptySpaces = reservationRecord.getMAX_CLIENTS() - totalDinners;
            reservationRecord.setEmptySpaces(emptySpaces);
        }

        reservationRecordRepository.save(reservationRecord);
    }

    @Override
    public Optional<List<Reservation>> getReservationsByClient(Client client) {
        return reservationRepository.findByClient(client);
    }

    @Override
    public Optional<List<Reservation>> getAcceptedReservationsByClient(Client client) {
        return reservationRepository.findByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Optional<List<Reservation>> getAllReservationsForDay(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }

    @Override
    public Optional<List<Reservation>>getAllReservationsForDayAndShift(LocalDate date, Shift shift) {
        return reservationRepository.findByReservationDateAndShift(date, shift);
    }

    @Override
    public Reservation cancelReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            LocalDate today = LocalDate.now();
            LocalDate reservationDate = reservation.getReservationDate();

            if (reservationDate.isEqual(today)) {
                throw new ReservationException("Cannot cancel a reservation on the same day.");
            }

            reservation.setReservationStatus(ReservationStatus.CANCELED);
            return reservationRepository.save(reservation);
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
    }

    @Override
    public Reservation confirmReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);
            return reservationRepository.save(reservation);
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
    }

    @Override
    public Reservation lossReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setReservationStatus(ReservationStatus.LOSS);
            return reservationRepository.save(reservation);
        } else {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
    }

    @Override
    public void updateReservationsToLoss(LocalDate actionDate, LocalTime currentTime) {
        LocalDate today = LocalDate.now();

        if (actionDate.equals(today)) {
            List<Shift> allowedShifts = getAllowedShifts(currentTime);
            for (Shift shift : allowedShifts) {
                Optional<List<Reservation>> optionalReservations =
                        reservationRepository.findByReservationDateAndShiftAndReservationStatus(actionDate, shift, ReservationStatus.ACCEPTED);
                if (optionalReservations.isPresent()) {
                    List<Reservation> reservations = optionalReservations.get();
                    for (Reservation reservation : reservations) {
                        reservation.setReservationStatus(ReservationStatus.LOSS);
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
        if (currentTime.isBefore(LocalTime.of(13, 0))) {
            allowedShifts.add(Shift.DAY1);
        }
        if (currentTime.isBefore(LocalTime.of(14, 0))) {
            allowedShifts.add(Shift.DAY2);
        }
        if (currentTime.isBefore(LocalTime.of(15, 0))) {
            allowedShifts.add(Shift.DAY3);
        }
        if (currentTime.isBefore(LocalTime.of(17, 0))) {
            allowedShifts.add(Shift.DAY4);
        }
        if (currentTime.isBefore(LocalTime.of(19, 0))) {
            allowedShifts.add(Shift.NIGHT1);
        }
        if (currentTime.isBefore(LocalTime.of(20, 0))) {
            allowedShifts.add(Shift.NIGHT2);
        }
        if (currentTime.isBefore(LocalTime.of(21, 0))) {
            allowedShifts.add(Shift.NIGHT3);
        }
        if (currentTime.isBefore(LocalTime.of(2, 0))) {
            allowedShifts.add(Shift.NIGHT4);
        }
        return allowedShifts;
    }

}
