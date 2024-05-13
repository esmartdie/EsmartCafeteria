package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ReservationTest {


    @Test
    public void testNoArgsConstructor() {
        Reservation reservation = new Reservation();
        assertEquals(null, reservation.getId());
        assertEquals(null, reservation.getClient());
        assertEquals(null, reservation.getDinners());
        assertEquals(null, reservation.getReservationDate());
        assertEquals(null, reservation.getShift());
        assertEquals(null, reservation.getReservationStatus());
    }

    @Test
    public void testAllArgsConstructor() {
        Client client = mock(Client.class);
        Integer dinners = 2;
        LocalDate reservationDate = LocalDate.of(2024, 5, 11);;
        Shift shift = Shift.DAY1;

        Reservation reservation = new Reservation(null, client, dinners, new ReservationRecord(), reservationDate, shift, ReservationStatus.PENDING);
        assertEquals(null, reservation.getId());
        assertEquals(client, reservation.getClient());
        assertEquals(dinners, reservation.getDinners());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(shift, reservation.getShift());
        assertEquals(ReservationStatus.PENDING, reservation.getReservationStatus());
    }
}
