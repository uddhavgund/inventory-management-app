package com.app.controller;


import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.app.model.Product;
import com.app.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.web.multipart.MultipartFile;
import java.io.File;


import com.app.service.S3Service;   // <-- ADD THIS


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {


    @Autowired
    ProductRepo repo;


    @Autowired
    private S3Service s3Service;   // <-- ADD THIS


    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }


    @PostMapping
    public Product add(@RequestBody Product p) {
        return repo.save(p);
    }


    // ------------------------------------------
    // CREATE PRODUCT + UPLOAD IMAGE TO AWS S3
    // ------------------------------------------
    @PostMapping("/upload")
    public Product upload(
            @RequestPart("product") Product p,
            @RequestPart("file") MultipartFile file
    ) throws Exception {


        // Save file temporarily
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        file.transferTo(tempFile);


        // Upload to S3
        String imageUrl = s3Service.uploadFile(tempFile);


        // Save S3 URL in DB
        p.imagePath = imageUrl;


        // Delete temp file
        tempFile.delete();


        return repo.save(p);
    }


    // ------------------------------------------
    // UPDATE PRODUCT WITHOUT TOUCHING IMAGE
    // ------------------------------------------
    @PutMapping("/{id}")
    public Product update(@PathVariable Integer id, @RequestBody Product p) {


        Product old = repo.findById(id).orElse(null);
        if (old == null) return null;


        old.name = p.name;
        old.quantity = p.quantity;


        return repo.save(old);
    }


    // ------------------------------------------
    // UPDATE ONLY IMAGE → UPLOAD TO S3
    // ------------------------------------------
    @PutMapping("/update-image/{id}")
    public Product updateImage(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file
    ) throws Exception {


        Product p = repo.findById(id).orElse(null);
        if (p == null) return null;


        // Temp save
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        file.transferTo(tempFile);


        // Upload S3
        String imageUrl = s3Service.uploadFile(tempFile);
        p.imagePath = imageUrl;


        tempFile.delete();


        return repo.save(p);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}
