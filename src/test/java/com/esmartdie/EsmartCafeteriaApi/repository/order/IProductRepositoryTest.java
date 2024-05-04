package com.esmartdie.EsmartCafeteriaApi.repository.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IProductRepositoryTest {

    @Autowired
    IProductRepository productRepository;

    Product product, product2;

    @BeforeEach
    void setUp() {
        product = new Product(null, "Test Product", true, 10, 100.0);
        product2 = new Product(null, "Test Product 2", true, 20, 200.0);

        productRepository.save(product);
        productRepository.save(product2);
    }

    @AfterEach
    void tearDown(){
        productRepository.deleteAll();
    }

    @Test
    public void testGetActiveProducts() {

        Collection<Product> activeProducts = productRepository.getActiveProducts();
        assertEquals(2, activeProducts.size());
    }



    @Test
    public void testFindById() {
        Optional<Product> optionalProduct = productRepository.findById(product.getId());
        assertTrue(optionalProduct.isPresent());
        assertEquals("Test Product", optionalProduct.get().getName());
    }

    @Test
    public void testFindByName() {
        Optional<Product> optionalProduct = productRepository.findByName("Test Product");
        assertTrue(optionalProduct.isPresent());
        assertEquals(product.getId(), optionalProduct.get().getId());
    }

    @Test
    public void testGetInActiveProducts() {

        Optional<Product> optionalProduct = productRepository.findById(product2.getId());
        Product productSaved = optionalProduct.get();
        productSaved.setAvailable(false);
        productRepository.save(productSaved);

        Collection<Product> inactiveProducts = productRepository.getInActiveProducts();
        assertEquals(1, inactiveProducts.size());
    }

}