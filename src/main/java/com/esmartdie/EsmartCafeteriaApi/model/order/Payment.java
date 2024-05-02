package com.esmartdie.EsmartCafeteriaApi.model.order;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import java.text.SimpleDateFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(targetEntity = Order.class)
    private Order order;
    @Enumerated(EnumType.STRING)
    private PaymentStatus STATUS;
    private SimpleDateFormat date;
}
