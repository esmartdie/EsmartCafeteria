package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

import java.util.Objects;


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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "record_id")
    private ReservationRecord record;

    @Column(name="reservation_date")
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    private Shift shift;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    public Reservation(Client client, Integer dinners, ReservationRecord record, LocalDate reservationDate, Shift shift) {
        this.client = client;
        this.dinners=dinners;
        this.record=record;
        this.reservationDate = reservationDate;
        this.shift = shift;
        this.reservationStatus = ReservationStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dinners=" + dinners +
                ", reservationDate=" + reservationDate +
                ", reservationStatus=" + reservationStatus +
                ", shift=" + shift +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id.equals(that.id) &&
                dinners.equals(that.dinners) &&
                reservationDate.equals(that.reservationDate) &&
                reservationStatus == that.reservationStatus &&
                shift == that.shift;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dinners, reservationDate, reservationStatus, shift);
    }

}
