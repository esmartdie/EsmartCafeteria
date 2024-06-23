package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.IllegalCalendarException;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationRecordServiceUnitTest {

    @Mock
    private IReservationRecordRepository reservationRecordRepository;

    @Mock
    private DTOConverter converter;

    @InjectMocks
    private ReservationRecordService reservationRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetReservationRecordsForMonth() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().getValue();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<ReservationRecord> mockRecords = new ArrayList<>();
        ReservationRecord record1 = new ReservationRecord();
        record1.setId(1L);
        record1.setReservationDate(LocalDate.of(year, month, 10));
        record1.setShift(Shift.DAY2);
        mockRecords.add(record1);

        ReservationRecordDTO dto1 = new ReservationRecordDTO();
        dto1.setId(1L);
        dto1.setReservationDate(LocalDate.of(year, month, 10));
        dto1.setShift(Shift.DAY2);
        dto1.setAvailableReservations(10);

        when(reservationRecordRepository.findAllByReservationDateBetween(startDate, endDate)).thenReturn(mockRecords);
        when(converter.createReservationRecordDTOFromReservationRecord(record1)).thenReturn(dto1);

        List<ReservationRecordDTO> result = reservationRecordService.getReservationRecordsForMonth(year, month);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(LocalDate.of(year, month, 10), result.get(0).getReservationDate());
    }

    @Test
    void testCreateMonthCalendarValid() {
        YearMonth yearMonth = YearMonth.now().plusMonths(1);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        when(reservationRecordRepository.findAllByReservationDateBetween(firstDay, lastDay)).thenReturn(new ArrayList<>());

        List<ReservationRecord> result = reservationRecordService.createMonthCalendar(yearMonth);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(reservationRecordRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateMonthCalendarExisting() {
        YearMonth yearMonth = YearMonth.now().plusMonths(1);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        List<ReservationRecord> existingRecords = Arrays.asList(new ReservationRecord(), new ReservationRecord());
        when(reservationRecordRepository.findAllByReservationDateBetween(firstDay, lastDay)).thenReturn(existingRecords);

        IllegalCalendarException thrown = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        assertEquals("The calendar is already opened.", thrown.getMessage());
    }

    @Test
    void testCreateMonthCalendarPast() {
        YearMonth yearMonth = YearMonth.now().minusMonths(1);

        IllegalCalendarException thrown = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        assertEquals("Forbidden action - Calendar is in the past", thrown.getMessage());
    }

    @Test
    void testCreateMonthCalendarLimit() {
        YearMonth yearMonth = YearMonth.now().plusMonths(4);

        IllegalCalendarException thrown = assertThrows(IllegalCalendarException.class, () -> {
            reservationRecordService.createMonthCalendar(yearMonth);
        });

        assertEquals("The limit to open a new calendar is two months.", thrown.getMessage());
    }

    @Test
    void testCreateMonthCalendar_WithDifferentShifts() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth());
        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(yearMonth);

        long day2ShiftCount = mayCalendar.stream()
                .filter(record -> record.getShift() == Shift.DAY2)
                .count();

        assertEquals(yearMonth.lengthOfMonth(), day2ShiftCount);
    }

    @Test
    void testCreateMonthCalendar_EmptySpaces() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth());
        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(yearMonth);

        boolean allRecordsHaveEmptySpaces = mayCalendar.stream()
                .allMatch(record -> record.getEmptySpaces() > 0);

        assertTrue(allRecordsHaveEmptySpaces);
    }

    @Test
    void testCreateMonthCalendar_FirstAndLastDay() {
        YearMonth yearMonth = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonth());
        List<ReservationRecord> mayCalendar = reservationRecordService.createMonthCalendar(yearMonth);

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        boolean firstDayRecordExists = mayCalendar.stream()
                .anyMatch(record -> record.getReservationDate().equals(firstDay));
        boolean lastDayRecordExists = mayCalendar.stream()
                .anyMatch(record -> record.getReservationDate().equals(lastDay));

        assertTrue(firstDayRecordExists);
        assertTrue(lastDayRecordExists);
    }
}