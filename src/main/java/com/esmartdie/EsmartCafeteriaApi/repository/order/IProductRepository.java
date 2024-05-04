package com.esmartdie.EsmartCafeteriaApi.repository.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface IProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.available = true")
    Collection<Product> getActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.available = false")
    Collection<Product> getInActiveProducts();

    Optional<Product> findById(Long productId);

    Optional<Product> findByName(String name);
}
