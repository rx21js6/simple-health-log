package jp.nauplius.app.shl.common.exception;

public class SimpleHealthLogException extends RuntimeException {
    public SimpleHealthLogException(Throwable e) {
        super(e);
    }

    public SimpleHealthLogException(String message) {
        super(message);
    }
}
