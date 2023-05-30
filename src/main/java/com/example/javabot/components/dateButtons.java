package com.example.javabot.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public interface dateButtons {
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    

    public static InlineKeyboardMarkup inlineKeyboardMarkup(){
        LocalDate currentDate = LocalDate.now();
        InlineKeyboardButton discard = new InlineKeyboardButton("Отмена");
        discard.setCallbackData("Discard");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowLine = new LinkedList<InlineKeyboardButton>();
        List<InlineKeyboardButton> rowLine2 = new LinkedList<InlineKeyboardButton>();
        rowLine2.add(discard);

        List<List<InlineKeyboardButton>> rowsLine = new LinkedList<List<InlineKeyboardButton>>();
        
        for (int i = 0; i < 4; i++){
            InlineKeyboardButton button = new InlineKeyboardButton(currentDate.format(formatter));
            button.setCallbackData(currentDate.format(formatter));
            rowLine.add(button);
            currentDate = currentDate.plusDays(1);
            
        }
        rowsLine.add(rowLine);
        rowsLine.add(rowLine2);
        inlineKeyboardMarkup.setKeyboard(rowsLine);
        
        return inlineKeyboardMarkup;
    }
}
