package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.IllegalCalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationRecordService implements IReservationRecordService{

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Override
    public List<ReservationRecordDTO> getReservationRecordsForMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<ReservationRecord> reservationRecords =
                reservationRecordRepository.findAllByReservationDateBetween(startDate, endDate);

        return reservationRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReservationRecordDTO mapToDTO(ReservationRecord reservationRecord) {
        ReservationRecordDTO dto = new ReservationRecordDTO();
        dto.setId(reservationRecord.getId());
        dto.setReservationDate(reservationRecord.getReservationDate());
        dto.setShift(reservationRecord.getShift());
        dto.setAvailableReservations(reservationRecord.getEmptySpaces());
        return dto;
    }

    @Override
    public List<ReservationRecord> createMonthCalendar(YearMonth yearMonth){

        validateCalendarDates(yearMonth);

        List<ReservationRecord> calendarDays = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> createDayRecords(yearMonth.atDay(day)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        reservationRecordRepository.saveAll(calendarDays);
        return calendarDays;
    }

    private void validateCalendarDates(YearMonth yearMonth) {

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth maxYearMonth = currentYearMonth.plusMonths(2);

        List<ReservationRecord> existingCalendar  =
                reservationRecordRepository.findAllByReservationDateBetween(firstDay, lastDay);

        if (!existingCalendar.isEmpty()) {
            throw new IllegalCalendarException("The calendar is already opened.");
        }

        if(!yearMonth.isBefore(maxYearMonth)){
            throw new IllegalCalendarException("The limit to open a new calendar is two months.");
        }

        if(yearMonth.isBefore(currentYearMonth)){
            throw new IllegalCalendarException("Forbidden action - Calendar is in the past");
        }

    }

    private List<ReservationRecord> createDayRecords(LocalDate date) {
        return Arrays.stream(Shift.values())
                .map(shift -> new ReservationRecord(date, shift))
                .collect(Collectors.toList());
    }


}
