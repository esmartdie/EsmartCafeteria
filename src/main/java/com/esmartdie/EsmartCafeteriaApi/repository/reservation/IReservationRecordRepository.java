package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRecordRepository extends JpaRepository<ReservationRecord, Long> {

    Optional<List<ReservationRecord>>findByReservationDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<ReservationRecord> findByReservationDateAndShift(SimpleDateFormat reservationDate, Shift shift);
}
