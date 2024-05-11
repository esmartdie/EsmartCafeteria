package com.esmartdie.EsmartCafeteriaApi.service.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import com.esmartdie.EsmartCafeteriaApi.repository.order.IProductRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService{

    @Autowired
    private IProductRepository productRepository;


    @Override
    public Product saveProduct(Product product) {
        log.info("Saving new product {} to the database", product.getName());
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductByName(String productName) {
        log.info("Fetching user {}", productName);
        return productRepository.findByName(productName);
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        log.info("Fetching user {}", productId);
        return productRepository.findById(productId);
    }

    @Override
    public Collection<Product> getActiveProducts(){
        log.info("Fetching all active products ");
        return productRepository.getActiveProducts();
    }

    @Override
    public Collection<Product> getInActiveProducts(){
        log.info("Fetching all inactive products ");
        return productRepository.getInActiveProducts();
    }

    @Override
    public Product updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setAvailable(updatedProduct.getAvailable());
        existingProduct.setAvailableQuantity(updatedProduct.getAvailableQuantity());
        existingProduct.setPrice(updatedProduct.getPrice());

        return productRepository.save(existingProduct);
    }
}
