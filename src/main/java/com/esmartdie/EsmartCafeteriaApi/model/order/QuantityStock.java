package com.esmartdie.EsmartCafeteriaApi.model.order;

import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.EAGER;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Employee.class)
    @Column(name="internal_user")
    private Employee internalUser;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name="initial_quantity")
    private Integer initialQuantity;
    @Column(name="operation_quantity")
    private Integer operationQuantity;
    @Column(name="final_quantity")
    private Integer finalQuantity;
    private String description;

}
