package cn.hashq.netpoststation.exception;

public class InvalidFrameException extends RuntimeException {

    public InvalidFrameException() {
    }

    public InvalidFrameException(String message) {
        super(message);
    }

    public InvalidFrameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFrameException(Throwable cause) {
        super(cause);
    }

    public InvalidFrameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
