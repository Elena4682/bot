package pro.sky.telegrambot.listener;

import pro.sky.telegrambot.repository.NotificationRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ScheduleCommand implements Command{
    private final Logger logger = LoggerFactory.getLogger(ScheduleCommand.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTERN = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final TelegramBot bot;
    private final NotificationRepository repository;

    public ScheduleCommand(TelegramBot bot,NotificationRepository repository){
        this.bot = bot;
        this.repository = repository;
    }

    @Override
    public void handle(Update update){
        var matcher = PATTERN.matcher(update.message().text());
        if (matcher.matches()){
            var dateTime = parse(matcher.group(1));
            if (dateTime==null){
                var chatId = update.message().chat().id();
                bot.execute(new SendMessage(chatId," The date format is specified incorrectly!"));
                return;
            }
            var text = matcher.group(3);
            var chatId = update.message().chat().id();
            var saved = repository.save(new pro.sky.telegrambot.entity.NotificationTask(text,chatId,dateTime));
            bot.execute(new SendMessage(chatId," Notification is scheduled!"));
            logger.info("Notification saved: {}", Optional.ofNullable(saved));
        }
    }

    @Override
    public boolean ifSuitable(Update update){
        return Optional.of(update)
                .map(Update::message)
                .map(Message::text)
                .map(PATTERN::matcher)
                .map(Matcher::matches)
                .orElse(false);
    }
    private LocalDateTime parse(String text){
        try {
            return LocalDateTime.parse(text,DATE_TIME_FORMATTERN);
        } catch (DateTimeException e){
            logger.error("Cannot parse text to date: {}", text);
        }
        return null;
    }
}
