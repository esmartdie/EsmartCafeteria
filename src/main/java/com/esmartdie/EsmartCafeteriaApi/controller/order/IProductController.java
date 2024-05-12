package com.esmartdie.EsmartCafeteriaApi.controller.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

public interface IProductController {

    Collection<Product> getProducts();

    Collection<Product> getInactiveProducts();

    ResponseEntity<?> saveProduct(@RequestBody Product product);

    void updateProducts(@PathVariable Long id, @RequestBody Product updatedProduct);
}
