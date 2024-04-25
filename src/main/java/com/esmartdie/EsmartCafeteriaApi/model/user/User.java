package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean active;
    @OneToOne
    private Role role;


}
