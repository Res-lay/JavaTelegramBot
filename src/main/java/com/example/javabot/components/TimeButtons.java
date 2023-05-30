package com.example.javabot.components;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface TimeButtons {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    public static InlineKeyboardMarkup inlineKeyboardMarkup(LocalTime time){
        LocalTime temp = time;
        for (int i = 0; i < 3; i ++){
            
            switch(i){
                case 0:
                    temp = time.minusMinutes(15);
                    InlineKeyboardButton timeButton = new InlineKeyboardButton(time.format(formatter));
                    timeButton.setCallbackData(time.format(formatter));
                    
                    break;
            }    
        }   
        ;
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        return keyboard;
    }
}
