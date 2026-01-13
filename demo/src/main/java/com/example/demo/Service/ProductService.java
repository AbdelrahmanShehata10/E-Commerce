package com.example.demo.Service;

import com.example.demo.Models.Product;
import com.example.demo.Repo.Product_Repo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {

private final Product_Repo Product_Repo;
    public List<Product> advancedSearch(
            String name,
            String category,
            Double minPrice,
            Double maxPrice,
            Boolean onSale
    ) {
        return Product_Repo.advancedSearch(
                name, category, minPrice, maxPrice, onSale
        );
    }

    public List<Product> GetAllProducts(){

      return   Product_Repo.findAll();

    }


   public ResponseEntity<Product>AddProduct(@RequestBody Product p ){



       Product savedProduct = Product_Repo.save(p);

       return ResponseEntity.ok(savedProduct);

   }


    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {

        if (!Product_Repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Product_Repo.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    public List<Product> getSortedProducts(
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return Product_Repo.findAll(sort);
    }
    public ResponseEntity<Product> updateProduct(int id, Product updatedProduct) {
        var product = Product_Repo.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        // Update only the fields that are provided (not null)
        if (updatedProduct.getName() != null) {
            product.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            product.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            product.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getCategory() != null) {
            product.setCategory(updatedProduct.getCategory());
        }
        if (updatedProduct.getStock() != null) {
            product.setStock(updatedProduct.getStock());
        }
        if (updatedProduct.getImageUrl() != null) {
            product.setImageUrl(updatedProduct.getImageUrl());
        }
        if (updatedProduct.getOnSale() != null) {
            product.setOnSale(updatedProduct.getOnSale());
        }
        // Add any other fields your Product entity has

        Product saved = Product_Repo.save(product);
        return ResponseEntity.ok(saved);
    }

}
