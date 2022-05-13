package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    NotificationTask findByTime(LocalDateTime time);

}
