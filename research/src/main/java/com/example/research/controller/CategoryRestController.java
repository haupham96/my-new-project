package com.example.research.controller;

import com.example.research.entity.Category;
import com.example.research.repository.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryRestController {

    @Autowired
    private ICategoryRepository repo;

    @Autowired
    private ICategoryRepository repository;

    @GetMapping
    public List<Category> getList(){
        return repository.getList();
    }

    @PostMapping()
    public Category save(@RequestBody Category category){
        repo.insert(category);
        return category;
    }

}
