package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    private Integer emptySpaces;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "record")
    List<Reservation> reservationList = new ArrayList<>();

    @Column(name="reservation_date")
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
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
