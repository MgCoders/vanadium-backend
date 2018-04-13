package coop.magnesium.vanadium.utils.ex;

/**
 * Created by rsperoni on 18/11/17.
 */
public class MagnesiumBdNotFoundException extends Exception {
    public MagnesiumBdNotFoundException() {
        super();
    }

    public MagnesiumBdNotFoundException(String message) {
        super(message);
    }

    public MagnesiumBdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumBdNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumBdNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
