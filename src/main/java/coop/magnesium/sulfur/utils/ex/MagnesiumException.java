package coop.magnesium.sulfur.utils.ex;

/**
 * Created by rsperoni on 28/10/17.
 */
public class MagnesiumException extends Exception {

    public MagnesiumException() {
        super();
    }

    public MagnesiumException(String message) {
        super(message);
    }

    public MagnesiumException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
