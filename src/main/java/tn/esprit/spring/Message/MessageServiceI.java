package tn.esprit.spring.Message;

import java.util.List;

public interface MessageServiceI {

    List<Messages> getMessages(Long senderId, Long recipientId);

    void sendMessage(Long senderId, Long recipientId, String messageBody) ;

}