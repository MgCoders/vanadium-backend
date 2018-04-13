package coop.magnesium.vanadium.utils.ex;

/**
 * Created by rsperoni on 28/10/17.
 */
public class MagnesiumNotFoundException extends MagnesiumException {
    public MagnesiumNotFoundException() {
        super();
    }

    public MagnesiumNotFoundException(String message) {
        super(message);
    }

    public MagnesiumNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
