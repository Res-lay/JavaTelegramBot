package com.example.javabot.Repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.javabot.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.appointments WHERE u.id = :id")
    Optional<User> fetchByIdWithAppointments(@Param("id") Long id);

    
}
