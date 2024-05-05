package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationService;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @PostMapping("/clients/create")
    public ResponseEntity<String> createReservation(@RequestBody Reservation request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (!hasPermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to create reservations.");
        }
        try {
            reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Reservation created successfully.");
        } catch (ReservationException e) {
            return ResponseEntity.badRequest().body("Failed to create reservation: " + e.getMessage());
        }
    }

    @GetMapping("/clients/my-reservations")
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Optional<List<Reservation>> optionalReservationList = reservationService.getReservationsByClient(client);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/clients/my-active-reservations")
    public ResponseEntity<List<Reservation>> getMyActiveReservation(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Optional<List<Reservation>> optionalReservationList =  reservationService.getAcceptedReservationsByClient(client);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservationOptional = reservationService.getReservationById(id);

        return reservationOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/day")
    public ResponseEntity<List<Reservation>> getAllReservationsForDay(@RequestParam LocalDate date) {
        Optional<List<Reservation>> optionalReservationList = reservationService.getAllReservationsForDay(date);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/day-shift")
    public ResponseEntity<List<Reservation>> getAllReservationsForDayAndShift(@RequestParam LocalDate date, @RequestParam Shift shift) {
        Optional<List<Reservation>> optionalReservationList = reservationService.getAllReservationsForDayAndShift(date, shift);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
