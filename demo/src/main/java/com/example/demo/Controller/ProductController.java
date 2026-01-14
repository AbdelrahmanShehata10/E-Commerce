package com.example.demo.Controller;

import com.example.demo.Models.Product;
import com.example.demo.Service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // ==================== PUBLIC ENDPOINTS ====================

    @GetMapping("/search")
    public List<Product> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean onSale
    ) {
        return productService.advancedSearch(name, category, minPrice, maxPrice, onSale);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.GetAllProducts();
    }

    @GetMapping("/sorted")
    public List<Product> getSortedProducts(
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return productService.getSortedProducts(sortBy, direction);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Product> getProductById(@PathVariable int id) {
//        return productService.getProductById(id);
//    }

    // ==================== ADMIN ONLY ENDPOINTS ====================

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return productService.AddProduct(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id,
            @RequestBody Product product
    ) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        return productService.deleteProduct(id);
    }

//    @PatchMapping("/{id}/stock")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Product> updateStock(
//            @PathVariable int id,
//            @RequestParam int quantity
//    ) {
//        return productService.updateStock(id, quantity);
//    }
}
