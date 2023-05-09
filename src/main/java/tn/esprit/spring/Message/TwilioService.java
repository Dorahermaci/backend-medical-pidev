package tn.esprit.spring.Message;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TwilioService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(TwilioService.class);

    private final String accountSid = "AC50745c8dde5a7de392660b5f1c2d5efe";
    private final String authToken = "15e5bb32e5dc5cc2d3bebc33975c88a8";

    private final TwilioRestClient client = new TwilioRestClient.Builder(accountSid, authToken).build();
    private final AppUserRepository appUserRepository;

    public TwilioService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void sendMessage(String recipientNumber, String message) {
        Message.creator(
                        new PhoneNumber(recipientNumber),
                        new PhoneNumber("+12706793605"),
                        message)
                .create();
    }

    public List<Messages> getMessages() {
        List<Messages> messages = new ArrayList<>();
        List<Message> twilioMessages = (List<Message>) Message.reader().read();

        for (Message twilioMessage : twilioMessages) {
            String senderNumber = String.valueOf(twilioMessage.getFrom());
            String recipientNumber = twilioMessage.getTo();

            Optional<AppUser> sender = appUserRepository.findByPhonenumber(senderNumber);
            Optional<AppUser> recipient = appUserRepository.findByPhonenumber(recipientNumber);

            if (sender.isPresent() && recipient.isPresent()) {
                Messages message = new Messages();
                message.setBody(twilioMessage.getBody());
                message.setSender(sender.get());
                message.setRecipient(recipient.get());
                messages.add(message);
            }
        }
        return messages;
    }


}
