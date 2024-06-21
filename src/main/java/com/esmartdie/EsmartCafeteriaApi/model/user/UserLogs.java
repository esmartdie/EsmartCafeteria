package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @PastOrPresent
    private LocalDate sessionStartDate;

    @PastOrPresent
    private LocalTime sessionStartTime;

    @PastOrPresent
    private LocalDate sessionEndDate;

    @PastOrPresent
    private LocalTime sessionEndTime;
}
