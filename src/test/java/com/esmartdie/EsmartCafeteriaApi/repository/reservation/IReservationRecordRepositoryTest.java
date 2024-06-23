package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class IReservationRecordRepositoryTest {

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    private int year = LocalDate.now().getYear();
    private int month = LocalDate.now().getMonth().plus(1).getValue();



    @AfterEach
    public void tearDown(){
        reservationRecordRepository.deleteAll();
    }


    @Test
    public void testFindByReservationDateBetween() {

        ReservationRecord reservationRecord1 = new ReservationRecord();
        reservationRecord1.setReservationDate(LocalDate.of(year, month, 1));
        reservationRecord1.setShift(Shift.DAY1);
        reservationRecordRepository.save(reservationRecord1);

        ReservationRecord reservationRecord2 = new ReservationRecord();
        reservationRecord2.setReservationDate(LocalDate.of(year, month, 3));
        reservationRecord2.setShift(Shift.NIGHT2);
        reservationRecordRepository.save(reservationRecord2);

        ReservationRecord reservationRecord3 = new ReservationRecord();
        reservationRecord3.setReservationDate(LocalDate.of(year, month, 5));
        reservationRecord3.setShift(Shift.DAY3);
        reservationRecordRepository.save(reservationRecord3);

        reservationRecord2.setReservationList(new ArrayList<>());


        LocalDate startDate = LocalDate.of(year, month, 2);
        LocalDate endDate = LocalDate.of(year, month, 4);
        List<ReservationRecord> result = reservationRecordRepository.findAllByReservationDateBetween(startDate, endDate);


        assertEquals(1, result.size());
        assertEquals(reservationRecord2, result.get(0));
    }

    @Test
    public void testFindByReservationDateAndShift() {
        LocalDate date = LocalDate.of(year, month, 10);
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