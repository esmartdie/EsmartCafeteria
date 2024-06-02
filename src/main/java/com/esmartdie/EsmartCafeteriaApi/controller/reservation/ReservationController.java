package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.GenericApiResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController implements IReservationController{

    @Autowired
    private ReservationService reservationService;


    @PostMapping("/users/clients/reservation/create")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Override
    public ResponseEntity<?> createReservation(@RequestBody NewReservationDTO request) {

        ReservationDTO reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericApiResponseDTO(true, "Reservation created successfully", reservation));
    }

    @GetMapping("/users/clients/reservation/my-reservations")
    @Override
    public ResponseEntity<List<ReservationDTO>> getMyReservations(Authentication authentication) {

        Client client = reservationService.getClientFromAuthentication(authentication);

        try {
            List<ReservationDTO> reservations = reservationService.getReservationsByClient(client);
            if (reservations.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/clients/reservation/my-active-reservations")
    @Override
    public ResponseEntity<List<ReservationDTO>> getMyActiveReservation(Authentication authentication) {
        Client client = reservationService.getClientFromAuthentication(authentication);

        try {
            List<ReservationDTO> reservations = reservationService.getAcceptedReservationsByClient(client);
            if (reservations.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/moderator/reservation/{id}")
    @Override
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservationDTO = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationDTO);
    }

    @GetMapping("/moderator/reservation/day")
    @Override
    public ResponseEntity<List<ReservationDTO>> getAllReservationsForDay(@RequestParam LocalDate date) {
        List<ReservationDTO> reservationDTOList = reservationService.getAllReservationsForDay(date);
        return ResponseEntity.ok(reservationDTOList);
    }

    @GetMapping("/moderator/reservation/day-shift")
    @Override
    public ResponseEntity<List<ReservationDTO>> getAllReservationsForDayAndShift(@RequestParam LocalDate date, @RequestParam Shift shift) {
        List<ReservationDTO> reservationDTOList = reservationService.getAllReservationsForDayAndShift(date,shift);
        return ResponseEntity.ok(reservationDTOList);
    }

    @PutMapping("/users/clients/reservation/{id}/cancel")
    @Override
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, Authentication authentication) {

        Client client = reservationService.getClientFromAuthentication(authentication);

        ReservationDTO cancelledReservation = reservationService.cancelReservation(id, client);
        return ResponseEntity.status(HttpStatus.OK).body(new GenericApiResponseDTO(true, "Reservation cancelled successfully", cancelledReservation));

    }

    @PatchMapping("/moderator/reservation/{reservationId}/updateStatus")
    @Override
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long reservationId, @RequestBody ReservationStatusUpdatedDTO request) {

        ReservationDTO updatedReservation = reservationService.updateReservationStatus(reservationId, request);
        return ResponseEntity.ok(new GenericApiResponseDTO(true, "Reservation updated successfully", updatedReservation));
    }

    @PutMapping("/moderator/reservation/massiveReservationUpdatingToLoss")
    @Override
    public ResponseEntity<?> updateReservationsMassivelyToLoss(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actionDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime) {

        List<ReservationDTO> reservationDTOList = reservationService.updateReservationsToLoss(actionDate, currentTime);
        return ResponseEntity.ok(new GenericApiResponseDTO(true, "Reservation updated successfully", reservationDTOList));

    }


}
