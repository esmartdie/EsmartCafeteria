package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IReservationRecordRepositoryTest {

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;


    @AfterEach
    public void tearDown(){
        reservationRecordRepository.deleteAll();
    }


    @Test
    public void testFindByReservationDateBetween() {

        ReservationRecord reservationRecord1 = new ReservationRecord();
        reservationRecord1.setReservationDate(LocalDate.of(2024, 5, 1));
        reservationRecord1.setShift(Shift.DAY1);
        reservationRecordRepository.save(reservationRecord1);

        ReservationRecord reservationRecord2 = new ReservationRecord();
        reservationRecord2.setReservationDate(LocalDate.of(2024, 5, 3));
        reservationRecord2.setShift(Shift.NIGHT2);
        reservationRecordRepository.save(reservationRecord2);

        ReservationRecord reservationRecord3 = new ReservationRecord();
        reservationRecord3.setReservationDate(LocalDate.of(2024, 5, 5));
        reservationRecord3.setShift(Shift.DAY3);
        reservationRecordRepository.save(reservationRecord3);

        reservationRecord2.setReservationList(new ArrayList<>());


        LocalDate startDate = LocalDate.of(2024, 5, 2);
        LocalDate endDate = LocalDate.of(2024, 5, 4);
        Optional<List<ReservationRecord>> result = reservationRecordRepository.findByReservationDateBetween(startDate, endDate);

        assertTrue(result.isPresent());
        List<ReservationRecord> records = result.get();
        assertEquals(1, records.size());
        assertEquals(reservationRecord2, records.get(0));
    }

    @Test
    public void testFindByReservationDateAndShift() {
        LocalDate date = LocalDate.of(2024, 5, 10);
        Shift shift = Shift.DAY2;

        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setReservationDate(date);
        reservationRecord.setReservationList(new ArrayList<>());
        reservationRecord.setShift(shift);

        reservationRecordRepository.save(reservationRecord);

        Optional<ReservationRecord> result = reservationRecordRepository.findByReservationDateAndShift(date, shift);

        assertTrue(result.isPresent());
        assertEquals(reservationRecord, result.get());
    }


}