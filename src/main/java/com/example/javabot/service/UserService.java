package com.example.javabot.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.javabot.Repos.UserRepo;
import com.example.javabot.models.User;



@Service
public class UserService{

    @Autowired
    UserRepo userRepo;
    
    
    public List<User> findById(Long id){
        Optional<User> user = userRepo.findById(id);
        return user.map(Collections::singletonList).orElse(Collections.emptyList());
    }


    public Optional<User> fetchByIdWithAppointments(Long id){
        return userRepo.fetchByIdWithAppointments(id);
    }

    public void save(User user){
        userRepo.save(user);
    }
    
}
