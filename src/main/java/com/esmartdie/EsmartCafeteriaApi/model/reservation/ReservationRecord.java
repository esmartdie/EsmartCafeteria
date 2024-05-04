package com.esmartdie.EsmartCafeteriaApi.model.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="max_tables")
    private final Integer MAX_CLIENTS=50;

    @Column(name="empty_spaces")
    private Integer emptySpaces;

    @OneToMany
    List<Reservation> reservationList;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="reserve_date")
    private SimpleDateFormat reservationDate;

    @Enumerated(EnumType.STRING)
    private Shift shift;
}
