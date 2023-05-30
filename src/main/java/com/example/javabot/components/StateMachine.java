package com.example.javabot.components;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.javabot.service.AppointmentService;
import com.example.javabot.service.UserService;

import lombok.Data;

@Data
@Controller
public class StateMachine {

    @Autowired
    UserService userService;
    @Autowired
    AppointmentService appointmentService;

    private BotState currentState;
    private LocalDate date;
    private LocalTime time;
    private String telephone;

    public StateMachine(){
        currentState = BotState.START;
    }
    public void changeState(){
        switch (currentState){
            case START:
                currentState = BotState.SELECT_DATE;
                break;
            case SELECT_DATE:
                currentState = BotState.SELECT_TIME;
                break;
            case SELECT_TIME:
                currentState = BotState.ENTER_CONTACT_INFO;
                break;
            case ENTER_CONTACT_INFO:
                currentState = BotState.START;
                break;
        }
    }

    public void deleteAppointmentState(){
        switch (currentState) {
            case START:
                currentState = BotState.SELECT_APPOINTMENT;
                break;
            default:
                currentState = BotState.START;
                break;
        }
    }

    public void changeAppointmentState(){
        switch (currentState){
            case START:
                this.currentState = BotState.APPOINTMENT_DATE;
                break;
            default:
                this.currentState = BotState.START;
        }
    }
    
    public void discardState(){
        this.currentState = BotState.START;
    }

    public String getState(){
        return currentState.name();
    }
}


enum BotState{
    START,
    SELECT_DATE,
    SELECT_TIME,
    SELECT_APPOINTMENT,
    ENTER_CONTACT_INFO,
    APPOINTMENT_DATE,
}

