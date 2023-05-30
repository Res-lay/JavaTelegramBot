package com.example.javabot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.javabot.components.Buttons;
import com.example.javabot.components.MainKeyboards;
import com.example.javabot.components.StateMachine;
import com.example.javabot.components.TelephoneButton;
import com.example.javabot.components.TimeButtons;
import com.example.javabot.components.dateButtons;
import com.example.javabot.config.BotConfig;
import com.example.javabot.models.Appointment;
import com.example.javabot.models.User;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InsBot extends TelegramLongPollingBot{
    final BotConfig botConfig;
    private Map<Long, StateMachine> stateMachineMap = new HashMap<>();
    
    @Autowired
    UserService userService;
    @Autowired
    AppointmentService appointmentService;

    public InsBot(BotConfig botConfig){
        this.botConfig = botConfig;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId = 0;
        long userId = 0;
        String userName = "";
        String recievedMessage;
        Contact contact = null;
        if (update.hasMessage()){

            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getFirstName();
            contact = update.getMessage().getContact();

            if (update.getMessage().hasText() || contact != null){
                recievedMessage = update.getMessage().getText();
                System.out.println(recievedMessage + "from hasText");
                botAnswerUtils(recievedMessage, chatId, userName, contact);
            }
           
        }
        
        else if (update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            contact = update.getCallbackQuery().getMessage().getContact();

            recievedMessage = update.getCallbackQuery().getData();

             

            botAnswerUtils(recievedMessage, chatId, userName, contact);
        }

        updateDB(userId, userName);

    }

    private void startBot(long chatId, String userName){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет " + userName + ", это бот-помошник!\nЕсли ты хочешь предупредить о визите, то можно сделать это, нажав на кнопку 'Визит'.\nЕсли есть вопросы, нажми на кнопку 'Помощь', там ответы на часто задаваемы вопросы.");
        message.setReplyMarkup(Buttons.inlineKeyboardMarkup());

        try{
            execute(message);
            log.info("Reply send");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private StateMachine getStateMachine(Long userId){
        if (stateMachineMap.containsKey(userId)){
            return stateMachineMap.get(userId);
        }
        else{
            StateMachine stateMachine = new StateMachine();
            stateMachineMap.put(userId, stateMachine);
            return stateMachine;        
        }
        
    }

    private void botAnswerUtils(String recievedMessage, long chatId, String userName, Contact contact){
        StateMachine stateMachine = getStateMachine(chatId);
        if (stateMachine.getState() != "SELECT_APPOINTMENT" && stateMachine.getState() != "SELECT_DATE" && stateMachine.getState() !="SELECT_TIME" && stateMachine.getState() != "ENTER_CONTACT_INFO"  && stateMachine.getState() != "APPOINTMENT_DATE"){
            switch (recievedMessage){
                case "/start":
                    startBot(chatId, userName);
                    break;
                case "/help":
                    sendHelpText(chatId);
                    break;
                case "/appointment":
                    startAppointment(chatId, userName);
                    break;
                case "/user":
                    getUserInformation(chatId);
                    break;
                case "/schedule":
                    getSchedule(chatId);
                    break;
                case "Часто задаваемые вопросы":
                    getFrequentQuestions(chatId);
                    break;
                case "Связь с сотрудником":
                    getWorkerContat(chatId);
                    break;
                case "Узнать свободное время для записи":
                    getReservedDates(chatId);
                    break;
                case "Удалить запись":
                    deleteAppointment(chatId);
                    break;
                case "Назад":
                    getBack(chatId);
                    break;
                default: log.info("Unexpected message");
            }
        }
        else{
            switch (stateMachine.getState()){
                case "SELECT_DATE":
                    setDate(chatId, userName, recievedMessage);
                    break;
                case "SELECT_TIME":
                    setTime(chatId, userName, recievedMessage);
                    break;
                case "ENTER_CONTACT_INFO":
                    setTelephoneNumber(chatId, userName, recievedMessage, contact);
                    break;
                case "APPOINTMENT_DATE":
                    getAppointmentsInfo(chatId, recievedMessage);
                    break;
                case "SELECT_APPOINTMENT":
                    deleteCurrentAppointment(chatId, recievedMessage, stateMachine);
                    break;
            }
        }
    }

    private void getFrequentQuestions(long chatId) {
        SendMessage message = new SendMessage();
        message.setText("Здесь будут часто задаваемые вопросы");
        message.setChatId(chatId);  
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void getSchedule(long chatId) {
        SendMessage message = new SendMessage();
        message.setText("Здесь будет расписание");
        message.setChatId(chatId);  
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void deleteCurrentAppointment(long chatId, String recievedMessage, StateMachine stateMachine) {
        SendMessage message = new SendMessage();
        
        message.setText(recievedMessage + " запись успешно удалена");
        message.setChatId(chatId);
        message.setReplyMarkup(TelephoneButton.createHideKeyboard());
        User user = userService.fetchByIdWithAppointments(chatId).orElse(null);
        if (user != null){
            int index = Integer.parseInt(recievedMessage) - 1;
            List<Appointment> appointments = user.getAppointments();
            Appointment appointment = appointments.get(index);
            appointmentService.delelteById(appointment.getId());
        }
        stateMachine.deleteAppointmentState();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void deleteAppointment(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Введите номер записи, которую хотите удалить");
        message.setReplyMarkup(MainKeyboards.getDiscardButton());
        StateMachine state = getStateMachine(chatId);
        state.deleteAppointmentState();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void getUserInformation(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String text = "";
        User user = userService.fetchByIdWithAppointments(chatId).orElse(null);
        if (user != null && user.getAppointments().size() != 0){
            text = "Ваши записи:\n";
            int i = 1;
            for (Appointment appointment : user.getAppointments()){
                text += String.valueOf(i) + ". Запись на " + appointment.getAppointmentDate() + " c " + appointment.getAppointmentTimeStart() + " по " + appointment.getAppointmentTimeEnd() + "\n"; 
                i ++;
            }
            
        }
        else{
            text = "У вас нет активных записей";
        }
        message.setText(text);
        message.setReplyMarkup(MainKeyboards.getDeletKeyboardMarkup());
        try {
            execute(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void getAppointmentsInfo(long chatId, String recievedMessage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate parsedDate = LocalDate.parse(recievedMessage, formatter); 

        List<Appointment> appointments = appointmentService.getByAppointmentDate(parsedDate);
        Collections.sort(appointments, Comparator.comparing(Appointment::getAppointmentTimeStart));

        StateMachine stateMachine = getStateMachine(chatId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(TelephoneButton.createHideKeyboard());
        SendMessage message1 = new SendMessage();
        message1.setChatId(chatId);
        message1.setText("Это список записей на " + recievedMessage + "\nВыбирайте время, которого нет в данном списке, при создании записи");
        String text = "";
        if(appointments.size() != 0){
            for (Appointment appointment : appointments){
                text += "Запись на " + appointment.getAppointmentTimeStart() + " - " + appointment.getAppointmentTimeEnd() + "\n";
            }
        }
        else{
            text = "На данную дату отсутствуют записи";
        }
        message.setText(text);
        try {
            execute(message1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            execute(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        stateMachine.changeAppointmentState();
    }

    private void getReservedDates(Long chatId) {
        SendMessage message = new SendMessage();
        message.setText("Введите дату(в формате 11.11.2023) для проверки свободного времени");
        StateMachine stateMachine = getStateMachine(chatId);
        stateMachine.changeAppointmentState();
        message.setChatId(chatId);
        message.setReplyMarkup(MainKeyboards.getDiscardButton());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void getBack(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Восспользуйтесь кнопкой Меню, которая находиться слева от поля ввода текста");
        message.setReplyMarkup(TelephoneButton.createHideKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void setTelephoneNumber(long chatId, String userName, String recievedMessage, Contact contact) {    
        if (recievedMessage != null && recievedMessage.equals("Отмена ")){
            SendMessage message = new SendMessage();
            StateMachine state = getStateMachine(chatId);
            state.discardState();
            message.setText("Отмена создания записи\nВосспользуйтесь кнопкой Меню, которая находиться слева от поля ввода текста");
            message.setReplyMarkup(TelephoneButton.createHideKeyboard());
            message.setChatId(chatId);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

            return;
        }
        StateMachine stateMachine = getStateMachine(chatId);

        User user = userService.fetchByIdWithAppointments(chatId).orElse(null);
        user.setPhoneNumber(contact.getPhoneNumber());
        userService.save(user);
    
        SendMessage messageWithInformation = setAppointment(stateMachine, contact.getPhoneNumber(), user);
        SendMessage menuMessage = getMenuMessage(chatId);

        stateMachine.setTelephone(contact.getPhoneNumber());

        stateMachine.changeState();

        try {
            execute(messageWithInformation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

        try {
            execute(menuMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void getWorkerContat(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Контакты сотрудника:\nЛеонид Михайлович\nКонтактный номер телефона: +79788117072\n");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage setAppointment(StateMachine stateMachine, String phone, User user){
        
        SendMessage messageWithInformation = new SendMessage();

        messageWithInformation.setChatId(user.getId());
        messageWithInformation.setText("Ваша запись готова!!!\nИнформация о записи:\nДата: " + stateMachine.getDate() + "\nВремя: " + stateMachine.getTime() + "\nЖдем вас!");
        messageWithInformation.setReplyMarkup(TelephoneButton.createHideKeyboard());

        Appointment appointment = new Appointment();
        appointment.setPhoneNumber(phone);
        appointment.setAppointmentDate(stateMachine.getDate());
        appointment.setAppointmentTimeStart(stateMachine.getTime());
        appointment.setAppointmentTimeEnd(stateMachine.getTime().plusMinutes(15));
        appointment.setUser(user);
        appointment.setUserName(user.getUserName());

        appointmentService.save(appointment);

        return messageWithInformation;
    }
    
    private void setTime(long chatId, String userName, String recievedMessage) {
        if (recievedMessage.equals("Отмена")){
            SendMessage message = new SendMessage();
            StateMachine state = getStateMachine(chatId);
            state.discardState();
            message.setText("Отмена создания записи\nВосспользуйтесь зеленой кнопкой Меню");
            message.setChatId(chatId);
            message.setReplyMarkup(TelephoneButton.createHideKeyboard());

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

            return;
        }
        SendMessage message = new SendMessage();
        User user = userService.fetchByIdWithAppointments(chatId).orElse(null);
        StateMachine stateMachine = getStateMachine(chatId);
        message.setChatId(chatId);
        LocalTime parsedTime = LocalTime.parse(recievedMessage);
        if (appointmentService.isFree(parsedTime, stateMachine.getDate())){
            if (user.getPhoneNumber() != null){
                stateMachine.changeState();
                stateMachine.setTime(parsedTime);
                SendMessage infMessage = setAppointment(stateMachine, user.getPhoneNumber(), user);    
                message = getMenuMessage(chatId);
                
                try {
                    execute(infMessage);
                } catch (Exception e) {
                    log.error(recievedMessage, e.getMessage());
                }

            }else{
                stateMachine.setTime(parsedTime);
                message.setText("Вы выбрали " + recievedMessage + "\nНажмите пожалуйста на кнопку\n'Поделиться номером телефона'\nЭто необходимо для обртной связи");
                message.setReplyMarkup(TelephoneButton.replyKeyboardMarkup());
            }
            stateMachine.changeState();
            
        }else{
            message.setText("К сожалению данное время уже занято - " + recievedMessage + "\nВы можете попробовать выбрать время, на 15-20 минут позже или раньше");
            message.setReplyMarkup(MainKeyboards.getDiscardButton());
        }
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void startAppointment(long chatId, String userName) {
        StateMachine stateMachine = getStateMachine(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите дату для записи или введите свою собственную");
        sendMessage.setReplyMarkup(dateButtons.inlineKeyboardMarkup());
        
        stateMachine.changeState();

        try{
            execute(sendMessage);
            log.info("appointemt is starting");
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }
    private void setDate(long chatId, String userName, String recievedMessage){
        if (recievedMessage.equals("Отмена") || recievedMessage.equals("Discard")){
            SendMessage message = new SendMessage();
            StateMachine state = getStateMachine(chatId);
            state.discardState();
            message.setText("Отмена создания записи\nВосспользуйтесь зеленой кнопкой Меню");
            message.setChatId(chatId);
            message.setReplyMarkup(TelephoneButton.createHideKeyboard());

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }

            return;
        }
        StateMachine stateMachine = getStateMachine(chatId);
        log.info(recievedMessage);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate parsedDateTime = LocalDate.parse(recievedMessage, formatter); 
        stateMachine.setDate(parsedDateTime);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы выбрали " + recievedMessage + ", теперь введите время, когда вам будет удобно прийти (В формате 00:00)");
        message.setReplyMarkup(MainKeyboards.getDiscardButton());
        stateMachine.changeState();

        try{
            execute(message);
            log.info("Date was chosed " + userName + " " + recievedMessage);
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }

    }

    private void sendHelpText(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите интересующий вас раздел");
        sendMessage.setReplyMarkup(MainKeyboards.getHelpKeyboard());


        try{
            execute(sendMessage);
            log.info("Reply send");
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken(){
        return botConfig.getBotToken();
    }

    private void updateDB(long userId, String userName) {

        if(userService.fetchByIdWithAppointments(userId).isEmpty()){
            User user = new User();
            user.setId(userId);
            user.setUserName(userName);

            userService.save(user);
            log.info("Added to DB: " + user);
        } 
    }

    private SendMessage getMenuMessage(Long chatId){
        SendMessage menuMessage = new SendMessage();

        menuMessage.setChatId(chatId);
        menuMessage.setText("Выберите интересующий вас пункт");
        menuMessage.setReplyMarkup(Buttons.inlineKeyboardMarkup());
        
        return menuMessage; 
    }
    
}

