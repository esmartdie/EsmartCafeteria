package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservationController {

    ResponseEntity<String> createReservation(@RequestBody NewReservationDTO request);

    ResponseEntity<List<ReservationDTO>> getMyReservations(Authentication authentication);

    ResponseEntity<List<ReservationDTO>> getMyActiveReservation(Authentication authentication);

    ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id);

    ResponseEntity<List<ReservationDTO>> getAllReservationsForDay(@RequestParam LocalDate date);

    ResponseEntity<List<ReservationDTO>> getAllReservationsForDayAndShift(@RequestParam LocalDate date, @RequestParam Shift shift);

    ResponseEntity<?> cancelReservation(@PathVariable Long id, Authentication authentication);

    ResponseEntity<?> updateReservationStatus(@PathVariable Long reservationId, @RequestBody ReservationStatusUpdatedDTO request);


    ResponseEntity<?> updateReservationsMassivelyToLoss(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actionDate,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime);
}
