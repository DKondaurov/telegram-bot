package pro.sky.telegrambot.service.impl;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public NotificationTask addTask(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
        return null;
    }

    @Override
    public NotificationTask findMessage(LocalDateTime time) {
        return notificationTaskRepository.findByTime(time);
    }

}
