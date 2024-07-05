package edu.school21.sockets.services;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

@Component
public class MessagesServiceImpl implements MessagesService{
    private final MessagesRepository<Message> messagesRepository;

    @Autowired
    public MessagesServiceImpl(@Qualifier("usersRepositoryImpl") MessagesRepository<Message> messageRepository) {
        this.messagesRepository = messageRepository;
    }

    @Override
    public long CreateMessage(User user, String text) throws SQLException {
        Message message = new Message();
        message.setAuthor(user);
        message.setRoomIdentifier(1);
        message.setText(text);
        Date currentDate = new Date();
        message.setDateTime(currentDate);
        messagesRepository.save(message);
        return message.getIdentifier();
    }

    @Override
    public List<Message> GetMessages(Date date) {
        return messagesRepository.findAllAfterDateTime(date);
    }

    @Override
    public Date GetLastMessageDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().getActualMinimum(Calendar.YEAR));
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date minDate = calendar.getTime();

        List<Message> tempList = GetMessages(minDate);
        if (tempList.size() > 0) {
            minDate = tempList.get(tempList.size() - 1).getDateTime();
        }
        return minDate;
    }
}
