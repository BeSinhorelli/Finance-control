package com.finance.app.service;

import com.finance.app.model.Category;
import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import com.finance.app.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public Category create(String name, String type, User user) {
        Category category = new Category();
        category.setName(name);
        category.setType(TransactionType.valueOf(type));
        category.setUser(user);
        return categoryRepository.save(category);
    }
    
    public List<Category> findAll(User user) {
        return categoryRepository.findByUserOrUserIsNull(user);
    }
    
    public List<Category> findByType(TransactionType type, User user) {
        return categoryRepository.findByTypeAndUser(type, user);
    }
}