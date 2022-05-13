package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskService notificationTaskService;
    private final NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;
    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService, NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskService = notificationTaskService;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            TelegramBot bot = new TelegramBot(token);
            Integer chatId = Math.toIntExact(update.message().chat().id());
            Long id = Long.valueOf(update.message().messageId());
            String messageText = parseMessage(update.message().text(), chatId);
            SendMessage message = new SendMessage(chatId, messageText);
            SendResponse response = bot.execute(message);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        String response = "";
        long id = 0;
        notificationTaskService.findMessage(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        NotificationTask notificationTask = notificationTaskService.findMessage(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        if (notificationTask.getText() != null) {
            response = notificationTask.getText();
            id = notificationTask.getChat();
            SendMessage message = new SendMessage(id, response);
            SendResponse response1 = telegramBot.execute(message);
        }

    }

    public String parseMessage(String messageText, long chatId) {
        String response = "";

        if (messageText.equals("/start")) {
            response = "Привет, я бот-напоминалка. Когда император Марк Аврелий выходил на улицы Римы на прогул" +
                    "ку, он брал с собой слугу, который шел следом и периодически негромко обращался к императо" +
                    "ру, напоминая: «Марк Аврелий, ты всего лишь человек...». Так скажи: о чём напоминать тебе?";
        } else {
            Pattern pattern = Pattern.compile("([\\d+.]{3}[\\d+.]{3}[\\d+.]{4}\\s[\\d+:]{5})\\s(\\W*)");
//            Pattern pattern = Pattern.compile("([\\d\\.\\:\\s]{16})\\s([\\W+]+)");
            Matcher matcher = pattern.matcher(messageText);
            if (matcher.matches()) {
                String date = matcher.group(1);
                String text = matcher.group(2);
                NotificationTask notificationTask = new NotificationTask();
                notificationTask.setText(text);
                notificationTask.setChat((int) chatId);
                notificationTask.setTime(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                notificationTaskService.addTask(notificationTask);
            } else {
                response = "Формат сообщения: \n\n" + "ДД.ММ.ГГГГ ЧЧ:ММ Текст напоминия";
            }
            return response;
        }
        return response;
    }
}

