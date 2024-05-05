package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservation-records")
public class ReservationRecordController {

    @Autowired
    private ReservationRecordService reservationRecordService;

    @GetMapping("/month")
    public ResponseEntity<List<ReservationRecordDTO>> getReservationRecordsForMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        List<ReservationRecordDTO> reservationRecords = reservationRecordService.getReservationRecordsForMonth(year, month);
        return ResponseEntity.ok(reservationRecords);
    }
}
