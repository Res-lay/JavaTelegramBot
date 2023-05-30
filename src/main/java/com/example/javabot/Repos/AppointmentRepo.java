package com.example.javabot.Repos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.javabot.models.Appointment;

public interface AppointmentRepo extends JpaRepository<Appointment, Long>{
    Appointment findByAppointmentTimeStart(LocalDateTime time);
    List<Appointment> getByAppointmentDate(LocalDate date);
}
