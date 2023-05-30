package com.example.javabot.components;


import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public interface TelephoneButton {
    
    KeyboardButton contactButton = new KeyboardButton("Поделиться номером телефона");
    KeyboardButton discard = new KeyboardButton("Отмена");
    public static ReplyKeyboardMarkup replyKeyboardMarkup(){
        contactButton.setRequestContact(true);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(contactButton);
        keyboardRow.add(discard);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setKeyboard(List.of(keyboardRow));
        return keyboard;
    }
    public static ReplyKeyboard createHideKeyboard() {

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();


        replyKeyboardRemove.setSelective(true);
        replyKeyboardRemove.setRemoveKeyboard(true);


        return replyKeyboardRemove;
    }
    
}
