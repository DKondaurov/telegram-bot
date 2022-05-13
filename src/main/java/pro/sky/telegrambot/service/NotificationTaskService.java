package pro.sky.telegrambot.service;

import org.hibernate.mapping.Collection;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;

public interface NotificationTaskService {

    NotificationTask addTask(NotificationTask notificationTask);

    NotificationTask findMessage(LocalDateTime time);
}
