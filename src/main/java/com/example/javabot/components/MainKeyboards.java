package com.example.javabot.components;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public interface MainKeyboards {
    

    KeyboardButton schedule = new KeyboardButton("График работы");
    KeyboardButton appointment = new KeyboardButton("Запись");
    KeyboardButton help = new KeyboardButton("Помощь");
    KeyboardButton delete = new KeyboardButton("Удалить запись");
    KeyboardButton frequentQuestions = new KeyboardButton("Часто задаваемые вопросы");
    KeyboardButton back = new KeyboardButton("Назад");
    KeyboardButton getAppointments = new KeyboardButton("Узнать свободное время для записи");
    KeyboardButton feedback = new KeyboardButton("Связь с сотрудником");

    

    public static ReplyKeyboardMarkup getKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(schedule);
        row.add(appointment);
        row.add(help);

        replyKeyboardMarkup.setKeyboard(List.of(row));
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getDeletKeyboardMarkup(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        row.add(delete);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(row));

        return markup;
    }

    public static ReplyKeyboardMarkup getHelpKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        row3.add(frequentQuestions);
        row.add(feedback);
        row.add(back);
        row2.add(getAppointments);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(row, row3, row2));
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getDiscardButton(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton discard = new KeyboardButton("Отмена");
        KeyboardRow row = new KeyboardRow();
        row.add(discard);

        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        
        return markup;
    }
}
