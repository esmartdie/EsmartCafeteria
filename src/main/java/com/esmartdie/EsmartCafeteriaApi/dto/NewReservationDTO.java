package com.esmartdie.EsmartCafeteriaApi.dto;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewReservationDTO {

    private Client client;

    @NotNull(message = "Number of dinners must be provided")
    @Min(value = 1, message = "There must be at least one diner")
    private Integer dinners;

    @NotNull(message = "Reservation date is required")
    @FutureOrPresent(message = "Reservation date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;

    @NotNull(message = "Shift is required")
    private Shift shift;

}
