package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;

import java.util.List;

public interface IReservationRecordService {
    List<ReservationRecordDTO> getReservationRecordsForMonth(int year, int month);
}
