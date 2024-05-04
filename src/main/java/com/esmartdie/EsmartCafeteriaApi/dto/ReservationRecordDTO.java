package com.esmartdie.EsmartCafeteriaApi.dto;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import jakarta.persistence.*;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
public class ReservationRecordDTO {

    private Integer id;

    private Integer availableReservations;

    private SimpleDateFormat reservationDate;

    private Shift shift;
}

