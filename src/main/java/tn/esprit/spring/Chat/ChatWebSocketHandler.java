package tn.esprit.spring.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserService;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("chatWebSocketHandler")
public class ChatWebSocketHandler extends TextWebSocketHandler {

    //@Autowired
    //private AuthenticationFacadeImpl authenticationFacade;

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final AppUserService medicalStaffService;

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    public ChatWebSocketHandler(ObjectMapper objectMapper, ChatService chatService, AppUserService medicalStaffService) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.medicalStaffService = medicalStaffService;
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Chat chatMessage = objectMapper.readValue(message.getPayload(), Chat.class);
        AppUser sender = medicalStaffService.getAppUserById(chatMessage.getId());
        AppUser recipient = medicalStaffService.getAppUserById(chatMessage.getId());

        if (sender == null || recipient == null || !chatService.canChat(sender, recipient)) {
            logger.error("Error sending chat message: invalid sender or recipient");
            return;
        }

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(recipient);
        chat.setMessage(chatMessage.getMessage()); // set the message to the one received in the POST request
        chat.setTimestamp(new Date());
        chat.setSessionId(session.getId()); // Set session ID

        chatService.addMessage(getChatRoomId(sender.getId(), recipient.getId()), chat);

        TextMessage response = new TextMessage(objectMapper.writeValueAsString(chat));
        session.sendMessage(response);
    }


    private String getChatRoomId(Long senderId, Long recipientId) {
        return Stream.of(senderId, recipientId)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("_"));
    }
}
