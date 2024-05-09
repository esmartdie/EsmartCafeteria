package com.esmartdie.EsmartCafeteriaApi.service.order;

import com.esmartdie.EsmartCafeteriaApi.model.order.Product;
import com.esmartdie.EsmartCafeteriaApi.repository.order.IProductRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSaveProduct() {
        Product product = new Product(null, "Test Product", true, 10, 100.0);
        when(productRepository.save(product)).thenReturn(product);
        Product savedProduct = productService.saveProduct(product);

        verify(productRepository, times(1)).save(product);
        assertEquals(product, savedProduct);
    }

    @Test
    void testGetProductByName() {
        when(productRepository.findByName("Test Product")).
                thenReturn(Optional.of(new Product(1L, "Test Product", true,
                        10, 100.0)));
        Optional<Product> productOptional = productService.getProductByName("Test Product");


        verify(productRepository, times(1)).findByName("Test Product");
        assertEquals("Test Product", productOptional.get().getName());
    }

    @Test
    void testGetProductByName_sadPath() {
        when(productRepository.findByName("Nonexistent Product")).thenReturn(Optional.empty());
        Optional<Product> productOptional = productService.getProductByName("Nonexistent Product");

        verify(productRepository, times(1)).findByName("Nonexistent Product");
        assertTrue(productOptional.isEmpty());
    }



    @Test
    void testGetProductById() {
        when(productRepository.findByName("Test Product")).
                thenReturn(Optional.of(new Product(1L, "Test Product", true,
                        10, 100.0)));
        Optional<Product> productOptional = productService.getProductByName("Test Product");


        verify(productRepository, times(1)).findByName("Test Product");
        assertEquals("Test Product", productOptional.get().getName());
    }

    @Test
    void testGetProductById_sadPath() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Product> productOptional = productService.getProductById(999L);

        verify(productRepository, times(1)).findById(999L);
        assertTrue(productOptional.isEmpty());
    }

    @Test
    void testGetActiveProducts_happyPath() {
        List<Product> activeProducts = Arrays.asList(
                new Product(1L, "Product 1", true, 10, 100.0),
                new Product(2L, "Product 2", true, 20, 200.0)
        );
        when(productRepository.getActiveProducts()).thenReturn(activeProducts);

        Collection<Product> result = productService.getActiveProducts();

        verify(productRepository, times(1)).getActiveProducts();

        assertEquals(activeProducts.size(), result.size());
        assertTrue(result.containsAll(activeProducts));
    }

    @Test
    void testGetActiveProducts_sadPath() {
        when(productRepository.getActiveProducts()).thenReturn(Collections.emptyList());
        Collection<Product> result = productService.getActiveProducts();

        verify(productRepository, times(1)).getActiveProducts();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetInActiveProducts_happyPath() {
        List<Product> inactiveProducts = Arrays.asList(
                new Product(3L, "Product 3", false, 5, 50.0),
                new Product(4L, "Product 4", false, 15, 150.0)
        );
        when(productRepository.getInActiveProducts()).thenReturn(inactiveProducts);
        Collection<Product> result = productService.getInActiveProducts();

        verify(productRepository, times(1)).getInActiveProducts();
        assertEquals(inactiveProducts.size(), result.size());
        assertTrue(result.containsAll(inactiveProducts));
    }

    @Test
    void testGetInActiveProducts_sadPath() {
        when(productRepository.getInActiveProducts()).thenReturn(Collections.emptyList());
        Collection<Product> result = productService.getInActiveProducts();

        verify(productRepository, times(1)).getInActiveProducts();
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateProduct_happyPath() {
        Product existingProduct = new Product(1L, "Existing Product", true, 10, 100.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = new Product(1L, "Updated Product", false, 5, 50.0);
        Product result = productService.updateProduct(1L, updatedProduct);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(updatedProduct);
        assertEquals(updatedProduct, result);
    }

    @Test
    void testUpdateProduct_sadPath() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Product updatedProduct = new Product(999L, "Non-existent Product", false, 5, 50.0);
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(999L, updatedProduct));


        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }




}