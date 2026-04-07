package com.finance.app.controller;

import com.finance.app.model.Category;
import com.finance.app.model.User;
import com.finance.app.repository.UserRepository;
import com.finance.app.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    
    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }
    
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String name = body.get("name").toString();
            String type = body.get("type").toString();
            
            User user = getUser(userId);
            Category category = categoryService.create(name, type, user);
            
            response.put("success", true);
            response.put("category", category);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }
    
    @GetMapping
    public List<Category> findAll(@RequestParam Long userId) {
        User user = getUser(userId);
        return categoryService.findAll(user);
    }
}