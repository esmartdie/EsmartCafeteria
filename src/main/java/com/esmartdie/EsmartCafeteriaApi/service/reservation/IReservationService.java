package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IReservationService {
    Reservation createReservation(Reservation reservation);

    Optional<List<Reservation>> getReservationsByClient(Client client);

    Optional<List<Reservation>> getAcceptedReservationsByClient(Client client);

    Optional<Reservation> getReservationById(Long id);

    Optional<List<Reservation>> getAllReservationsForDay(LocalDate date);

    Optional<List<Reservation>>getAllReservationsForDayAndShift(LocalDate date, Shift shift);

    Reservation cancelReservation(Long reservationId);

    Reservation confirmReservation(Long reservationId);

    Reservation lossReservation(Long reservationId);

    void updateReservationsToLoss(LocalDate actionDate, LocalTime currentTime);
}
