package com.esmartdie.EsmartCafeteriaApi.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean available;
    @Column(name="available_quantity")
    private Integer availableQuantity;
    private Double price;
}
