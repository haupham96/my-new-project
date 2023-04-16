package com.example.mybatis.controller;

import com.example.mybatis.entity.Category;
import com.example.mybatis.repository.ICategoryRepository;
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
