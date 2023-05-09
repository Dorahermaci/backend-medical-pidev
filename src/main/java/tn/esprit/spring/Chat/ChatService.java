package tn.esprit.spring.Chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRole;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ChatService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatRepository chatRepository;

    private final Map<String, Queue<Chat>> chatRooms = new ConcurrentHashMap<>();


    private final ChatController chatController;

    @Autowired
    public ChatService( @Lazy ChatController chatController) {
        this.chatController = chatController;
    }
    public void addMessage(String roomId, Chat chat) {
        chat.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        Queue<Chat> chatRoom = chatRooms.computeIfAbsent(roomId, k -> new ConcurrentLinkedQueue<>());
        chatRoom.add(chat);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, chat);

        // Save the chat message to the database
        chatRepository.save(chat);
    }

    public List<Chat> getChatHistory(String roomId) {
        return new ArrayList<>(chatRooms.getOrDefault(roomId, new ConcurrentLinkedQueue<>()));
    }

    public boolean canChat(AppUser sender, AppUser recipient) {
        return sender.getAppUserRole() == AppUserRole.DOCTOR && recipient.getAppUserRole() == AppUserRole.LabrotoryManager;
    }
}
