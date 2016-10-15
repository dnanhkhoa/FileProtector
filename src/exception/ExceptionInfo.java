package exception;

public final class ExceptionInfo extends Exception {

    private static final long serialVersionUID = 1L;

    public ExceptionInfo() {
    }

    public ExceptionInfo(String message) {
        super(message);
    }

    public ExceptionInfo(Throwable cause) {
        super(cause);
    }

    public ExceptionInfo(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionInfo(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
