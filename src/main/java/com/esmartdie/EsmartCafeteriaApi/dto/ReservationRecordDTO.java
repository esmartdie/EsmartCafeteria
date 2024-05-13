package com.esmartdie.EsmartCafeteriaApi.dto;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRecordDTO {

    private Long id;

    private Integer availableReservations;

    private LocalDate reservationDate;

    private Shift shift;
}

