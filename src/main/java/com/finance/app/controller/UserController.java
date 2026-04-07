package com.finance.app.controller;

import com.finance.app.dto.LoginRequest;
import com.finance.app.dto.RegisterRequest;
import com.finance.app.model.User;
import com.finance.app.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserRepository userRepository;
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.existsByEmail(request.getEmail())) {
            response.put("error", "Email já cadastrado!");
            return response;
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        
        User saved = userRepository.save(user);
        
        response.put("message", "Cadastro realizado com sucesso!");
        response.put("userId", saved.getId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        return response;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        
        if (user == null) {
            response.put("error", "Usuário não encontrado!");
            return response;
        }
        
        if (!user.getPassword().equals(request.getPassword())) {
            response.put("error", "Senha incorreta!");
            return response;
        }
        
        response.put("message", "Login realizado com sucesso!");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        return response;
    }
}