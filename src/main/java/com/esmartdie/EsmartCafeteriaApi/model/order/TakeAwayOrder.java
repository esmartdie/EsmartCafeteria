package com.esmartdie.EsmartCafeteriaApi.model.order;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import java.text.SimpleDateFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TakeAwayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Client.class)
    private Client client;
    @OneToOne(targetEntity = Cart.class)
    private Cart cart;
    @Column(name="pickup_time")
    private SimpleDateFormat pickUpTime;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean active;
    @Enumerated(EnumType.STRING)
    private orderStatus STATUS;
    @Column(name="total_price")
    private Boolean totalPrice;
    @OneToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod paymentMethod;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean paid;
    private String userComments;
}
