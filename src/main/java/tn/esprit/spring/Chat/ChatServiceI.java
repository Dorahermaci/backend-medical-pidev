package tn.esprit.spring.Chat;

import tn.esprit.spring.AppUser.AppUser;

import java.util.List;

public interface ChatServiceI {



    void addMessage(String roomId, Chat chat);
    List<Chat> getChatHistory(String roomId);
    boolean canChat(AppUser sender, AppUser recipient);

}
