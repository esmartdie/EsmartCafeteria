package com.esmartdie.EsmartCafeteriaApi.dto;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyReservationDTO {

    private Long id;

    private ClientDTO clientDTO;

    private Integer dinners;

    private LocalDate reservationDate;

    private Shift shift;

    private ReservationStatus reservationStatus;
}
