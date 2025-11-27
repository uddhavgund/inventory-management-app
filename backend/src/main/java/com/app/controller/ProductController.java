package com.app.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.app.model.Product;
import com.app.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    ProductRepo repo;

    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Product add(@RequestBody Product p) {
        return repo.save(p);
    }

    // MAIN UPLOAD (Create product + image)
    @PostMapping("/upload")
    public Product upload(
            @RequestPart("product") Product p,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        File target = new File(uploadDir + fileName);
        file.transferTo(target);

        p.imagePath = "uploads/" + fileName;

        return repo.save(p);
    }

    // UPDATE PRODUCT WITHOUT REMOVING IMAGE
    @PutMapping("/{id}")
    public Product update(@PathVariable Integer id, @RequestBody Product p) {

        Product old = repo.findById(id).orElse(null);
        if (old == null) return null;

        old.name = p.name;
        old.quantity = p.quantity;

        // do NOT remove image
        return repo.save(old);
    }

    // UPDATE PRODUCT IMAGE ONLY
    @PutMapping("/update-image/{id}")
    public Product updateImage(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        Product p = repo.findById(id).orElse(null);
        if (p == null) return null;

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        File target = new File(uploadDir + fileName);
        file.transferTo(target);

        p.imagePath = "uploads/" + fileName;

        return repo.save(p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}
