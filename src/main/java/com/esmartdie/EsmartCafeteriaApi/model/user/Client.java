package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User{

    @Digits(integer = 1, fraction = 2)
    private double qualification;

    public Client(Long id, String name, String lastName, String email, String password, Boolean active, Role role) {
        super(id, name, lastName, email, password, active, role);
        setQualification(5.0);
        setRole(new Role(null,"ROLE_USER"));
    }



}
