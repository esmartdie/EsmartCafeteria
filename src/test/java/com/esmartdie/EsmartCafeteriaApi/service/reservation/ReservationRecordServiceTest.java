package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.IllegalCalendarException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationRecordServiceTest {
    @Mock
    private IReservationRecordRepository mockReservationRecordRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRecordService reservationRecordService;

    @AfterEach
    public void tearDown(){
        reservationRecordRepository.deleteAll();
    }


    @Test
    public void testGetReservationRecordsForMonth_HappyPath() {

        int year = 2024;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<ReservationRecord> mockRecords = Collections.singletonList(createMockReservationRecord());

        when(mockReservationRecordRepository.findAllByReservationDateBetween(startDate, endDate))
                .thenReturn(Optional.of(mockRecords));


        List<ReservationRecordDTO> result =  reservationRecordService.getReservationRecordsForMonth(year, month);

        assertEquals(mockRecords.size(), result.size());
        for (int i = 0; i < mockRecords.size(); i++) {
            assertDTOEquals(mockRecords.get(i), result.get(i));
        }
    }

    @Test
    public void testGetReservationRecordsForMonth_UnhappyPath() {

        int year = 2024;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        when(mockReservationRecordRepository.findAllByReservationDateBetween(startDate, endDate))
                .thenReturn(Optional.empty());

        List<ReservationRecordDTO> result = reservationRecordService.getReservationRecordsForMonth(year, month);

        assertTrue(result.isEmpty());
    }

    private ReservationRecord createMockReservationRecord() {
        ReservationRecord record = new ReservationRecord();
        record.setId(1L);
        record.setReservationDate(LocalDate.of(2024, 5, 10));
        record.setShift(Shift.DAY2);
        record.setEmptySpaces(10);
        return record;
    }

    private void assertDTOEquals(ReservationRecord record, ReservationRecordDTO dto) {
        assertEquals(record.getId(), dto.getId());
        assertEquals(record.getReservationDate(), dto.getReservationDate());
        assertEquals(record.getShift(), dto.getShift());
        assertEquals(record.getEmptySpaces(), dto.getAvailableReservations());
    }

    @Test
    public void testCreateAMonthlyCalendar_HappyPath(){

        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(YearMonth.of(2024,5));
        List<ReservationRecord> allMonth = reservationRecordRepository.findAll();

        assertEquals(248, allMonth.size());
    }

    @Test
    public void testCreateAMonthlyCalendar_HappyPathLimitCase(){

        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(YearMonth.of(2024,7));
        List<ReservationRecord> allMonth = reservationRecordRepository.findAll();

        assertEquals(248, allMonth.size());
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathFutureMonthExcessTwoMonth(){

        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(YearMonth.of(2024,8));;
        });

        String expectedMessage = "The limit to open a new calendar is two months.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathPastCalendar(){

        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(YearMonth.of(2024,4));;
        });

        String expectedMessage = "Forbidden action - Calendar is in the past";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathCalendarAlreadyExists(){

        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(YearMonth.of(2024,5));

        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(YearMonth.of(2024,5));;
        });

        String expectedMessage = "The calendar is already opened.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


}