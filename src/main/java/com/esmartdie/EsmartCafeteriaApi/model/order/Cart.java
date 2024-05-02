package com.esmartdie.EsmartCafeteriaApi.model.order;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Client.class)
    private Client client;
    @OneToMany(targetEntity = Product.class, fetch = FetchType.LAZY)
    private Collection<Product> products;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean active;

}
