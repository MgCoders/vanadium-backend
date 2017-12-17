package coop.magnesium.sulfur.system;

import java.util.List;

/**
 * Created by rsperoni on 17/12/17.
 */
public class MailEvent {

    private List<String> to;
    private String message;
    private String subject;

    public MailEvent(List<String> to, String message, String subject) {
        this.to = to;
        this.message = message;
        this.subject = subject;
    }

    public List<String> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }
}
