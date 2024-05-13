package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservationService {
    ReservationDTO createReservation(NewReservationDTO reservationDTO);

    List<ReservationDTO> getReservationsByClient(Client client);

    Client getClientFromAuthentication(Authentication authentication);

    List<ReservationDTO> getAcceptedReservationsByClient(Client client);

    ReservationDTO getReservationById(Long id);

    List<ReservationDTO> getAllReservationsForDay(LocalDate date);

    List<ReservationDTO> getAllReservationsForDayAndShift(LocalDate date, Shift shift);


    ReservationDTO cancelReservation(Long reservationId, Client client);


    ReservationDTO updateReservationStatus(Long reservationId, ReservationStatusUpdatedDTO reservationDTO);

    List<ReservationDTO>  updateReservationsToLoss(LocalDate actionDate, LocalTime currentTime);
}
