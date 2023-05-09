package tn.esprit.spring.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/{senderId}/{recipientId}")
    public ResponseEntity<?> sendMessage(@PathVariable Long senderId, @PathVariable Long recipientId, @RequestBody String messageBody) {
        messageService.sendMessage(senderId, recipientId, messageBody);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{senderId}/{recipientId}")
    public ResponseEntity<List<Messages>> getMessages(@PathVariable Long senderId, @PathVariable Long recipientId) {
        List<Messages> messages = messageService.getMessages(senderId, recipientId);
        return ResponseEntity.ok(messages);
    }

}
