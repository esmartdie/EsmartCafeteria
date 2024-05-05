package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReservationRecordServiceTest {
    @MockBean
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRecordService reservationRecordService;

    @Test
    public void testGetReservationRecordsForMonth_HappyPath() {

        int year = 2024;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<ReservationRecord> mockRecords = Collections.singletonList(createMockReservationRecord());

        when(reservationRecordRepository.findByReservationDateBetween(startDate, endDate))
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

        when(reservationRecordRepository.findByReservationDateBetween(startDate, endDate))
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
}