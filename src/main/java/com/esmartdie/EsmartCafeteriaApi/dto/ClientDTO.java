package com.esmartdie.EsmartCafeteriaApi.dto;

import com.esmartdie.EsmartCafeteriaApi.utils.ValidationGroups;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ClientDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    @Min(value = 1, message = "Rating must be greater than or equal to 1", groups = ValidationGroups.RatingInfo.class)
    @Max(value = 10, message = "Rating must be less than or equal to 10", groups = ValidationGroups.RatingInfo.class)
    @Digits(integer = 2, fraction = 2, message = "Rating must not contain more than two decimal places", groups = ValidationGroups.RatingInfo.class)
    private double rating;
    private boolean active;

    public ClientDTO() {
    }

    public ClientDTO(Long id) {
        this.id = id;
    }

    public ClientDTO(Long id, String name, String lastName, String email, Boolean active) {
        this.id = id;
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.active=active;
        setRating(5);
    }

    public ClientDTO(String name, String lastName, String email, Boolean active, double rating) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.active=active;
        this.rating=rating;
    }

    public ClientDTO(String name, String lastName, String email) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
    }
}
