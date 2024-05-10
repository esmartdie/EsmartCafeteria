package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.YearMonthDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationRecordService;
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

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */
    @GetMapping("/empty_spaces_month")
    public ResponseEntity<List<ReservationRecordDTO>> getReservationRecordsForMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        List<ReservationRecordDTO> reservationRecords = reservationRecordService.getReservationRecordsForMonth(year, month);
        return ResponseEntity.ok(reservationRecords);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PostMapping("/create_month")
    @ResponseStatus(HttpStatus.CREATED)
    public String createCalendar(@RequestBody YearMonthDTO yearMonthDTO) {
        YearMonth yearMonth = yearMonthDTO.getYearMonth();

        List<ReservationRecord> openCalendar =  reservationRecordService.createMonthCalendar(yearMonth);

        return "The calendar for the month " + yearMonth + " has been created with a total of " + openCalendar.size() + " records.";
    };

}
