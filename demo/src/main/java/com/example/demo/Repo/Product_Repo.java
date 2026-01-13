package com.example.demo.Repo;

import com.example.demo.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Product_Repo extends JpaRepository<Product, Integer> {

    @Query(value = """
        SELECT * FROM product p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:onSale IS NULL OR p.on_sale = :onSale)
        """, nativeQuery = true)
    List<Product> advancedSearch(@Param("name") String name,
                                 @Param("category") String category,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("onSale") Boolean onSale);

    List<Product> findAll();


}