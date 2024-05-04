package com.esmartdie.EsmartCafeteriaApi.model.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    public void setUp() {
        Long id = 1L;
        String name = "Test Product";
        Boolean available = true;
        Integer availableQuantity = 10;
        Double price = 100.0;

        product = new Product(id, name, available, availableQuantity, price);
    }

    @Test
    public void testProductConstructor() {
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(true, product.getAvailable());
        assertEquals(10, product.getAvailableQuantity());
        assertEquals(100.0, product.getPrice());
    }

}