package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.CalendarCreationResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.YearMonthDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationRecordService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class ReservationRecordController {

    @Autowired
    private ReservationRecordService reservationRecordService;

    @GetMapping("/empty_spaces_month")
    public ResponseEntity<List<ReservationRecordDTO>> getReservationRecordsForMonth(
            @RequestParam("year") @Min(value = 2024, message = "Year must be greater than or equal to 1000") int year,
            @RequestParam("month") @Min(value = 1, message = "Month must be between 1 and 12") @Max(value = 12,
                    message = "Month must be between 1 and 12") int month) {
        List<ReservationRecordDTO> reservationRecords = reservationRecordService.getReservationRecordsForMonth(year, month);
        return ResponseEntity.ok(reservationRecords);
    }


    @PostMapping("/create_month")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CalendarCreationResponseDTO> createCalendar(@RequestBody YearMonthDTO yearMonthDTO) {
        YearMonth yearMonth = yearMonthDTO.getYearMonth();

        List<ReservationRecord> openCalendar =  reservationRecordService.createMonthCalendar(yearMonth);

        CalendarCreationResponseDTO response = new CalendarCreationResponseDTO(
                yearMonth.toString(),
                openCalendar.size()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    };

}
