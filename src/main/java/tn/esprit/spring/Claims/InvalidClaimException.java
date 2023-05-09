package tn.esprit.spring.Claims;

public class InvalidClaimException extends Exception {
    public InvalidClaimException(String message) {
        super(message);
    }
}
