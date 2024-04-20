package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name="last_name")
    private String lastName;
    private String email;
    private String password;
    private boolean active;
    private Role role;


}
