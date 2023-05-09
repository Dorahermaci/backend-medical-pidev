package tn.esprit.spring.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.AppUser.AppUser;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {

    List<Messages> findBySenderAndRecipientOrderByTimestampDesc(AppUser sender, AppUser recipient);

}