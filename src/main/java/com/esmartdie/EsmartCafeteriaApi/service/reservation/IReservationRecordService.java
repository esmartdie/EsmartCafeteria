package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;

import java.time.YearMonth;
import java.util.List;

public interface IReservationRecordService {
    List<ReservationRecordDTO> getReservationRecordsForMonth(int year, int month);

    List<ReservationRecord> createMonthCalendar(YearMonth yearMonth);
}
