package com.esmartdie.EsmartCafeteriaApi.model.user;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("Client")
public class Client extends User{

    private double rating;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<Reservation> reservations;

    public Client(Long id, String name, String lastName, String email, String password, Boolean active, Role role) {
        super(id, name, lastName, email, password, active, role);
        setRating(5.0);
    }

}
