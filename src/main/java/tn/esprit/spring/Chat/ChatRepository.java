

package tn.esprit.spring.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.AppUser.AppUserRole;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    //List<Chat> findBySenderAndReceiverIn(MedicalStaff sender, List<MedicalStaff> receivers);
    //List<Chat> findChatsBySenderRoleAndRecipient(Role doctor, Role laboratoryManager);

    List<Chat> findChatsBySenderAndRecipient(AppUserRole doctor, AppUserRole labrotoryManager);
}
