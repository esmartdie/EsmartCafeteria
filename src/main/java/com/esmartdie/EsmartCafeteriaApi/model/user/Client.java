package com.esmartdie.EsmartCafeteriaApi.model.user;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import jakarta.persistence.Entity;
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
public class Client extends User{

    private double rating;

    @OneToMany(mappedBy = "client")
    private List<ReservationStatus> reservations;

    public Client(Long id, String name, String lastName, String email, String password, Boolean active, Role role) {
        super(id, name, lastName, email, password, active, role);
        setRating(5.0);
        setRole(new Role(null,"ROLE_USER"));
    }



}
