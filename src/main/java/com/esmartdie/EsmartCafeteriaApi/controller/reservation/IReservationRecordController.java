package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.YearMonthDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IReservationRecordController {
    ResponseEntity<List<ReservationRecordDTO>> getReservationRecordsForMonth(
            @RequestParam("year") @Min(value = 2024, message = "Year must be greater than or equal to 1000") int year,
            @RequestParam("month") @Min(value = 1, message = "Month must be between 1 and 12") @Max(value = 12,
                    message = "Month must be between 1 and 12") int month);

    ResponseEntity<?> createCalendar(@RequestBody YearMonthDTO yearMonthDTO);
}
