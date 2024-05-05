package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        reservationRecord.setEmptySpaces(recalculateTotalDinners(reservation.getReservationDate(), reservation.getShift()));
        reservationRecordRepository.save(reservationRecord);

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

    private int recalculateTotalDinners(LocalDate reservationDate, Shift shift) {
        Optional<ReservationRecord> optionalReservationRecord = reservationRecordRepository.findByReservationDateAndShift(reservationDate, shift);
        ReservationRecord reservationRecord = optionalReservationRecord.orElseGet(() -> findOrCreateReservationRecord(reservationDate, shift));
        List<Reservation> reservations = reservationRecord.getReservationList();

        if (reservations == null) {
            return reservationRecord.getMAX_CLIENTS();
        }

        int totalDinners = reservations.stream().mapToInt(Reservation::getDinners).sum();
        return  reservationRecord.getMAX_CLIENTS() - totalDinners;
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

}
