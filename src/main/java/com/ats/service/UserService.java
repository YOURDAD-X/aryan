package com.ats.service;

import com.ats.dto.AuthRequest;
import com.ats.model.User;
import com.ats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // In a real app, use BCryptPasswordEncoder
        user.setEmail(request.getEmail());
        user.setCredits(10); // Start with 10 credits
        
        return userRepository.save(user);
    }

    public User login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(request.getPassword())) {
                return user;
            }
        }
        throw new RuntimeException("Invalid credentials!");
    }
}
