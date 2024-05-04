package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    private Integer dinners;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="reserve_date")
    private SimpleDateFormat reservationDate;

    @Enumerated(EnumType.STRING)
    private Shift shift;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    public Reservation(Client client, Integer dinners, SimpleDateFormat reservationDate, Shift shift) {
        this.client = client;
        this.dinners=dinners;
        this.reservationDate = reservationDate;
        this.shift = shift;
        this.reservationStatus = ReservationStatus.PENDING;
    }

}
