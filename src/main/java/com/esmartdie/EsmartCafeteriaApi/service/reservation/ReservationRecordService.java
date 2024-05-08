package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.IllegalCalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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

        Optional<List<ReservationRecord>> optionalReservationRecords =
                reservationRecordRepository.findAllByReservationDateBetween(startDate, endDate);

        if (optionalReservationRecords.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReservationRecord> reservationRecords = optionalReservationRecords.get();
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

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth maxYearMonth = currentYearMonth.plusMonths(2);

        Optional<List<ReservationRecord>> optionalOpenCalendar =
                reservationRecordRepository.findAllByReservationDateBetween(firstDay, lastDay);

        optionalOpenCalendar.ifPresent(openCalendar -> {
            if (!openCalendar.isEmpty()) {
                throw new IllegalCalendarException("The calendar is already opened.");
            }
        });

        if(!yearMonth.isBefore(maxYearMonth)){
            throw new IllegalCalendarException("The limit to open a new calendar is two months.");
        }

        if(yearMonth.isBefore(currentYearMonth)){
            throw new IllegalCalendarException("Forbidden action - Calendar is in the past");
        }


        List<Shift> shiftList = new ArrayList<>(Arrays.asList(Shift.values()));
        List<ReservationRecord> calendarDays = new ArrayList<>();

        LocalDate currentDay = firstDay;

        while (!currentDay.isAfter(lastDay)) {
            for (Shift shift : shiftList) {

                ReservationRecord calendarDay = new ReservationRecord();
                calendarDay.setReservationDate(currentDay);
                calendarDay.setShift(shift);

                reservationRecordRepository.save(calendarDay);
                calendarDays.add(calendarDay);
            }
            currentDay = currentDay.plusDays(1);
        }

        return calendarDays;
    }



}
