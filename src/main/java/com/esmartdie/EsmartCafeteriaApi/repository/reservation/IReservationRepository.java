package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findById(Long id);
    List<Reservation> findAllByClient(Client client);
    List<Reservation> findAllByClientAndReservationStatus(Client client, ReservationStatus reservationStatus);
    List<Reservation> findAllByReservationDate(LocalDate date);
    List<Reservation> findAllByReservationDateAndShift(LocalDate date, Shift shift);
    List<Reservation> findAllByReservationDateAndShiftAndReservationStatus(LocalDate date, Shift shift, ReservationStatus reservationStatus);
    Optional<Reservation> findByClientAndReservationDateAndShift(Client client, LocalDate reservationDate, Shift shift);
}
