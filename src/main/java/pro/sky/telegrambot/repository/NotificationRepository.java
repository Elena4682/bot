package pro.sky.telegrambot.repository;

import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.entity.NotificationTask;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<pro.sky.telegrambot.entity.NotificationTask,Long>{
    List<pro.sky.telegrambot.entity.NotificationTask> findAllByDateTime(LocalDateTime dateTime);

}
