package jp.nauplius.app.shl.common.exception;

public class DatabaseException extends SimpleHealthLogException {
    public DatabaseException(Exception e) {
        super(e);
    }
}
