package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.User;

import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> searchUsers(String name) {
        // TODO Assignment 7b: obtain a list of users with the given name
        //      from the userRepository and return the result (instead
        //      the empty list below).

        return userRepository.findByName(name);
    }

    // Assignment 7c: register a new user (must satisfy name constraints,
    //                and must not collide with an existing user name)
    @Transactional
    public User signUp(User user) {
        if (user == null || user.getName() == null || user.getName().length() < 4) {
            throw new RuntimeException("User name must be at least 4 characters long");
        }
        List<User> existing = userRepository.findByName(user.getName());
        if (existing != null && !existing.isEmpty()) {
            throw new RuntimeException("User with name '" + user.getName() + "' already exists");
        }
        User toSave = new User();
        toSave.setName(user.getName());
        return userRepository.save(toSave);
    }

}
