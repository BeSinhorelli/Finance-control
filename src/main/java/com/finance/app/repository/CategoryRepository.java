package com.finance.app.repository;

import com.finance.app.model.Category;
import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserOrUserIsNull(User user);
    List<Category> findByTypeAndUser(TransactionType type, User user);
    boolean existsByNameAndUser(String name, User user);
}