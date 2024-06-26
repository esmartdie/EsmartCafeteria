package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
public class ReservationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="max_tables")
    private final Integer MAX_CLIENTS=40;

    @Column(name="empty_spaces")
    @Positive
    private Integer emptySpaces;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "record")
    List<Reservation> reservationList = new ArrayList<>();

    @Column(name="reservation_date")
    @FutureOrPresent(message = "Reservation date must be in the present or future")
    @NotNull(message = "Reservation date must not be null")
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Shift must not be null")
    private Shift shift;


    public ReservationRecord() {
        setEmptySpaces(getMAX_CLIENTS());
    }

    public ReservationRecord(LocalDate date, Shift shift) {
        this.reservationDate=date;
        this.shift=shift;
        setEmptySpaces(MAX_CLIENTS);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReservationRecord other = (ReservationRecord) obj;
        return Objects.equals(this.getId(), other.getId()) &&
                Objects.equals(this.getReservationDate(), other.getReservationDate()) &&
                this.getShift() == other.getShift();
    }
}
