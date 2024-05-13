package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ReservationRecordTest {

    @Test
    public void testNoArgsConstructor() {
        ReservationRecord reservationRecord = new ReservationRecord();
        assertEquals(null, reservationRecord.getId());
        assertEquals(40, reservationRecord.getMAX_CLIENTS());
        assertEquals(40, reservationRecord.getEmptySpaces());
        assertEquals(new ArrayList<>(), reservationRecord.getReservationList());
        assertEquals(null, reservationRecord.getReservationDate());
        assertEquals(null, reservationRecord.getShift());
    }

    @Test
    public void testAllArgsConstructor() {
        Long id=1L;
        Integer maxClients = 40;
        Integer emptySpaces = 30;
        LocalDate reservationDate = LocalDate.of(2024, 5, 11);;
        Shift shift = Shift.DAY3;

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(mock(Reservation.class));

        ReservationRecord reservationRecord = new ReservationRecord(id, emptySpaces, reservationList, reservationDate, shift);
        assertEquals(id, reservationRecord.getId());
        assertEquals(40, reservationRecord.getMAX_CLIENTS());
        assertEquals(emptySpaces, reservationRecord.getEmptySpaces());
        assertEquals(reservationList, reservationRecord.getReservationList());
        assertEquals(reservationDate, reservationRecord.getReservationDate());
        assertEquals(shift, reservationRecord.getShift());
    }

}