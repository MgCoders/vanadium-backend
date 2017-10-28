package coop.magnesium.sulfur.utils.ex;

/**
 * Created by rsperoni on 28/10/17.
 */
public class MagnesiumSecurityException extends MagnesiumException {
    public MagnesiumSecurityException() {
        super();
    }

    public MagnesiumSecurityException(String message) {
        super(message);
    }

    public MagnesiumSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumSecurityException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumSecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
