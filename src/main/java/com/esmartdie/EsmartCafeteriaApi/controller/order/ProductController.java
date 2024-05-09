package com.esmartdie.EsmartCafeteriaApi.controller.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import com.esmartdie.EsmartCafeteriaApi.service.order.IProductService;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UpdateFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController implements IProductController{

    @Autowired
    private IProductService productService;

    @Override
    @GetMapping("/products/active")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Product> getProducts() {
        return productService.getActiveProducts();
    }

    @Override
    @GetMapping("/products/inactive")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Product> getInactiveProducts() {
        return productService.getInActiveProducts();
    }


    @Override
    @PostMapping("/products/create")
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {

        Optional<Product> existingProduct = productService.getProductById(product.getId());

        if (existingProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The product is already registered");
        }

        productService.saveProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("The product was created successfully");
    }

    @Override
    @PatchMapping("/products/{id}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProducts(@PathVariable Long id, @RequestBody Product updatedProduct) {

        try {
            Optional<Product> existingProductOptional = productService.getProductById(id);

            if (!existingProductOptional.isPresent()) {
                throw new ResourceNotFoundException("Product not found with id: " + id);
            }

            Product existingProduct = existingProductOptional.get();

            productService.updateProduct(id, updatedProduct);

        }catch (Exception e) {
            throw new UpdateFailedException("Error when try to update product with id: " + id, e);
        }
    }
}
