package coop.magnesium.sulfur.utils.ex;

/**
 * Created by rsperoni on 18/11/17.
 */
public class MagnesiumBdMultipleResultsException extends Exception {
    public MagnesiumBdMultipleResultsException() {
        super();
    }

    public MagnesiumBdMultipleResultsException(String message) {
        super(message);
    }

    public MagnesiumBdMultipleResultsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagnesiumBdMultipleResultsException(Throwable cause) {
        super(cause);
    }

    protected MagnesiumBdMultipleResultsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
