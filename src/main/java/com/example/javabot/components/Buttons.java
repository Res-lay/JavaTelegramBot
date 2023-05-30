package com.example.javabot.components;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface Buttons {
    public final static InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Запись");
    public final static InlineKeyboardButton GRAPHIC_BUTTON = new InlineKeyboardButton("График работы"); 
    public final static InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Помощь");

    public static InlineKeyboardMarkup inlineKeyboardMarkup(){
        START_BUTTON.setCallbackData("/appointment");
        GRAPHIC_BUTTON.setCallbackData("/graphic");
        HELP_BUTTON.setCallbackData("/help");

        List<InlineKeyboardButton> rowInline = List.of(START_BUTTON, HELP_BUTTON);
        List<InlineKeyboardButton> rowInline2 = List.of(GRAPHIC_BUTTON);
        List<List<InlineKeyboardButton>> rowsLine = List.of(rowInline, rowInline2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowsLine);
        
        return inlineKeyboardMarkup;
    }
}
