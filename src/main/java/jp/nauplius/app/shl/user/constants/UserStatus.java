package jp.nauplius.app.shl.user.constants;

public enum UserStatus {
    TEMPORARY(0), REGISTERED(1), DELETED(-1);

    private int status;

    UserStatus(int status) {
        this.status = status;
    }

    public int getInt() {
        return this.status;
    }
}
