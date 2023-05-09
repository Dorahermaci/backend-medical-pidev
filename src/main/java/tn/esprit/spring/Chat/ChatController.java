package tn.esprit.spring.Chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/chat")
public class ChatController {


    private final ChatService chatService;
    @Autowired
    private AppUserService medicalStaffService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    @Transactional
    @PostMapping("/{recipientId}/{senderId}")
    public ResponseEntity<Chat> sendMessage(@PathVariable Long recipientId, @PathVariable Long senderId, @RequestBody String message) {
        AppUser sender = medicalStaffService.getAppUserById(senderId);
        AppUser recipient = medicalStaffService.getAppUserById(recipientId);

        if (sender == null || recipient == null || !chatService.canChat(sender, recipient)) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(recipient);
        chat.setMessage(message);
        chat.setTimestamp(new Date());


        chatService.addMessage(getChatRoomId(sender.getId(), recipient.getId()), chat);
        return ResponseEntity.ok(chat);
    }


    @GetMapping("/{senderId}/{recipientId}/history")
    public ResponseEntity<List<Chat>> getChatHistory(@PathVariable Long senderId,@PathVariable Long recipientId) {
        AppUser sender = medicalStaffService.getAppUserById(senderId);
        AppUser recipient = medicalStaffService.getAppUserById(recipientId);

        if (sender == null || recipient == null || !chatService.canChat(sender, recipient)) {
            return ResponseEntity.badRequest().build();
        }

        List<Chat> chatHistory = chatService.getChatHistory(getChatRoomId(sender.getId(), recipient.getId()));
        return ResponseEntity.ok(chatHistory);
    }
    @GetMapping("/getchat/{senderId}/{recipientId}")
    private String getChatRoomId(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return Stream.of(senderId, recipientId)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("_"));
    }
}

