package com.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.business.entities.User;
import com.business.repositories.UserRepository;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get All Users
    public List<User> getAllUser() {
        List<User> users = (List<User>) this.userRepository.findAll();
        return users;
    }

    // Get Single User
    public User getUser(int id) {
        Optional<User> optional = this.userRepository.findById(id);
        User user = optional.get();
        return user;
    }

    // Update User
    public void updateUser(User user, int id) {
        user.setUserId(id);
        this.userRepository.save(user);
    }

    // Delete Single User
    public void deleteUser(int id) {
        this.userRepository.deleteById(id);
    }

    // Add User
    public void addUser(User user) {
        this.userRepository.save(user);
    }

    // Authenticate User based on email and password
    public boolean authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByUserEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getUserPassword().equals(password); // Verify password
        }
        return false; // If user is not found or password does not match
    }
}
