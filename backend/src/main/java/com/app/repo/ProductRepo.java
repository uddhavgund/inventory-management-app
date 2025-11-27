package com.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.model.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {}
