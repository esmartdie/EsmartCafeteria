package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.IllegalCalendarException;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationRecordServiceIntegrationTest {

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRecordService reservationRecordService;

    @Autowired
    private DTOConverter converter;

    @BeforeEach
    public void setUp() {
        reservationRecordRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        reservationRecordRepository.deleteAll();
    }

    @Test
    public void testGetReservationRecordsForMonth_HappyPath() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().plus(1));

        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(yearMonth);
        List<ReservationRecordDTO> result = reservationRecordService.getReservationRecordsForMonth(yearMonth.getYear(),
                yearMonth.getMonth().getValue());

        assertEquals(mayCalendar.size(), result.size());
        mayCalendar.forEach(record -> {
            ReservationRecordDTO dto = converter.createReservationRecordDTOFromReservationRecord(record);
            assertEquals(record.getId(), dto.getId());
            assertEquals(record.getReservationDate(), dto.getReservationDate());
            assertEquals(record.getShift(), dto.getShift());
            assertEquals(record.getEmptySpaces(), dto.getAvailableReservations());
        });
    }

    @Test
    public void testGetReservationRecordsForMonth_UnhappyPath() {
        int year = Year.now().getValue();
        int month = YearMonth.now().getMonth().getValue();

        List<ReservationRecordDTO> result = reservationRecordService.getReservationRecordsForMonth(year, month);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateAMonthlyCalendar_HappyPath() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().plus(1));
        List<ReservationRecord> currentCalendar = reservationRecordService.createMonthCalendar(yearMonth);
        List<ReservationRecord> allMonth = reservationRecordRepository.findAll();

        assertEquals(248, allMonth.size());
    }

    @Test
    public void testCreateAMonthlyCalendar_HappyPathLimitCase() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().plus(2));
        List<ReservationRecord> currentCalendar = reservationRecordService.createMonthCalendar(yearMonth);
        List<ReservationRecord> allMonth = reservationRecordRepository.findAll();

        assertEquals(248, allMonth.size());
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathFutureMonthExcessTwoMonth() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().plus(3));
        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        String expectedMessage = "The limit to open a new calendar is two months.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathPastCalendar() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().minus(1));

        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        String expectedMessage = "Forbidden action - Calendar is in the past";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCreateAMonthlyCalendar_UnHappyPathCalendarAlreadyExists() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth().plus(1));
        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(yearMonth);

        IllegalCalendarException thrownException = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        String expectedMessage = "The calendar is already opened.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}