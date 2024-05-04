package com.esmartdie.EsmartCafeteriaApi.service.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;

import java.util.Collection;
import java.util.Optional;

public interface IProductService {

    Product saveProduct(Product product);

    Optional<Product> getProductByName(String productName);

    Optional<Product> getProductById(Long productId);

    Collection<Product> getActiveProducts();

    Collection<Product> getInActiveProducts();

    Product updateProduct(Long productId, Product updatedProduct);
}
