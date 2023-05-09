package tn.esprit.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tn.esprit.spring.Claims.Claims;
import tn.esprit.spring.Claims.ClaimsRepository;


@Controller
public class DashboardController {

    @Autowired
    private ClaimsRepository claimRepository;

    @GetMapping("/")
    public String showDashboard(Model model) {
        int totalClaims = (int) claimRepository.count();
        int pendingClaims = claimRepository.countByStatus(Claims.ClaimStatus.PENDING);
        int inProgressClaims = claimRepository.countByStatus(Claims.ClaimStatus.IN_PROGRESS);
        int approvedClaims = claimRepository.countByStatus(Claims.ClaimStatus.APPROVED);
        int rejectedClaims = claimRepository.countByStatus(Claims.ClaimStatus.REJECTED);
        int completedClaims = claimRepository.countByStatus(Claims.ClaimStatus.COMPLETED);

        model.addAttribute("totalClaims", totalClaims);
        model.addAttribute("pendingClaims", pendingClaims);
        model.addAttribute("inProgressClaims", inProgressClaims);
        model.addAttribute("approvedClaims", approvedClaims);
        model.addAttribute("rejectedClaims", rejectedClaims);
        model.addAttribute("completedClaims", completedClaims);

        return "dashboard";
    }
}

