package tn.esprit.spring.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.AppUser.AppUserRole;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService implements MessageServiceI {

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void sendMessage(Long senderId, Long recipientId, String messageBody) {
        Optional<AppUser> sender = appUserRepository.findById(senderId);
        Optional<AppUser> recipient = appUserRepository.findById(recipientId);

        if (sender.isPresent() && recipient.isPresent()) {
            if (sender.get().getAppUserRole() == AppUserRole.DOCTOR && recipient.get().getAppUserRole() == AppUserRole.PATIENT) {
                String recipientNumber = "+216" + recipient.get().getPhonenumber().toString();
                twilioService.sendMessage(recipientNumber, messageBody);
                Messages message = new Messages();
                message.setSender(sender.get());
                message.setRecipient(recipient.get());
                message.setBody(messageBody);

                messageRepository.save(message);
            } else {
                throw new IllegalArgumentException("Sender and recipient roles are not valid for messaging.");
            }
        } else {
            throw new IllegalArgumentException("Invalid sender or recipient ID.");
        }
    }

    public List<Messages> getMessages(Long senderId, Long recipientId) {
        Optional<AppUser> sender = appUserRepository.findById(senderId);
        Optional<AppUser> recipient = appUserRepository.findById(recipientId);

        if (sender.isPresent() && recipient.isPresent()) {
            if (sender.get().getAppUserRole() == AppUserRole.DOCTOR && recipient.get().getAppUserRole() == AppUserRole.PATIENT) {
                return messageRepository.findBySenderAndRecipientOrderByTimestampDesc(sender.get(), recipient.get());
            } else {
                throw new IllegalArgumentException("Sender and recipient roles are not valid for messaging.");
            }
        } else {
            throw new IllegalArgumentException("Invalid sender or recipient ID.");
        }
    }


}