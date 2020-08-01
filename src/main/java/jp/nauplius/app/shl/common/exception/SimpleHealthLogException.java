package jp.nauplius.app.shl.common.exception;

public class SimpleHealthLogException extends RuntimeException {
    public SimpleHealthLogException(Exception e) {
        super(e);
    }
}
