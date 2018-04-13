package coop.magnesium.vanadium.utils.ex;

/**
 * Created by rsperoni on 18/11/17.
 */
public class MagnesiumBdAlredyExistsException extends Exception {
    public MagnesiumBdAlredyExistsException() {
        super();
    }

    public MagnesiumBdAlredyExistsException(String message) {
        super(message);
    }

    public MagnesiumBdAlredyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumBdAlredyExistsException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumBdAlredyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
