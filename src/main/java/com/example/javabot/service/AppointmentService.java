package com.example.javabot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.javabot.Repos.AppointmentRepo;
import com.example.javabot.models.Appointment;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepo;

    public Appointment getByAppointmentTime(LocalDateTime time){
        return appointmentRepo.findByAppointmentTimeStart(time);
    }

    

    public void save(Appointment appointment){
        appointmentRepo.save(appointment);
    }

    public boolean isFree(LocalTime time, LocalDate date){
        List<Appointment> appointments = appointmentRepo.getByAppointmentDate(date);
        LocalTime timeStart;
        LocalTime timeEnd;
        for (Appointment appointment : appointments){
            timeStart = appointment.getAppointmentTimeStart();
            timeEnd = appointment.getAppointmentTimeEnd();
            if (time.isBefore(timeEnd) && time.isAfter(timeStart)){
                return false;
            }
        }
        return true;
    }

    public void delelteById(Long id){
        appointmentRepo.deleteById(id);
    }



    public List<Appointment> getByAppointmentDate(LocalDate date) {
        return appointmentRepo.getByAppointmentDate(date);
    }
}
