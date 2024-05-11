package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationService;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationException;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */
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

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/clients/my-reservations")
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Optional<List<Reservation>> optionalReservationList = reservationService.getReservationsByClient(client);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/clients/my-active-reservations")
    public ResponseEntity<List<Reservation>> getMyActiveReservation(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Optional<List<Reservation>> optionalReservationList =  reservationService.getAcceptedReservationsByClient(client);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/employee/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservationOptional = reservationService.getReservationById(id);

        return reservationOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/employee/day")
    public ResponseEntity<List<Reservation>> getAllReservationsForDay(@RequestParam LocalDate date) {
        Optional<List<Reservation>> optionalReservationList = reservationService.getAllReservationsForDay(date);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/employee/day-shift")
    public ResponseEntity<List<Reservation>> getAllReservationsForDayAndShift(@RequestParam LocalDate date, @RequestParam Shift shift) {
        Optional<List<Reservation>> optionalReservationList = reservationService.getAllReservationsForDayAndShift(date, shift);

        return optionalReservationList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PutMapping("/clients/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Optional<Reservation> optionalReservation = reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (!reservation.getClient().equals(client)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to cancel this reservation.");
            }

            try {
                reservationService.cancelReservation(id);
                return ResponseEntity.ok("Reservation successfully canceled.");
            } catch (ReservationException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }catch (ReservationNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/employee/{reservationId}/confirm")
    public ResponseEntity<?> confirmReservation(@PathVariable Long reservationId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actionDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime) {
        try {
            Reservation confirmedReservation = reservationService.confirmReservation(reservationId, actionDate, currentTime);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(confirmedReservation);
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/employee/{reservationId}/loss")
    public ResponseEntity<?> lossReservation(@PathVariable Long reservationId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actionDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime) {
        try {
            Reservation confirmedReservation = reservationService.lostReservation(reservationId, actionDate, currentTime);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(confirmedReservation);
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PutMapping("/employee/updateLoss")
    public ResponseEntity<?> updateReservationsToLoss(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actionDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime currentTime) {
        try {
            reservationService.updateReservationsToLoss(actionDate, currentTime);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
