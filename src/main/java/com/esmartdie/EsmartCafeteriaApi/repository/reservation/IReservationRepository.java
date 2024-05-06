package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findById(Long id);
    Optional<List<Reservation>> findByClient(Client client);
    Optional<List<Reservation>> findByClientAndReservationStatus(Client client, ReservationStatus reservationStatus);
    Optional<List<Reservation>> findByReservationDate(LocalDate date);
    Optional<List<Reservation>> findByReservationDateAndShift(LocalDate date, Shift shift);
    Optional<List<Reservation>> findByReservationDateAndShiftAndReservationStatus(LocalDate date, Shift shift, ReservationStatus reservationStatus);
    Optional<Reservation> findByClientAndReservationDateAndShift(Client client, LocalDate reservationDate, Shift shift);
}
