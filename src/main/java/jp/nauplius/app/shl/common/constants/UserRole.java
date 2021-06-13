package jp.nauplius.app.shl.common.constants;

public enum UserRole {
    ADMIN(0), NORMAL(1);

    private final int id;

    private UserRole(final int id) {
        this.id = id;
    }

    public int getInt() {
        return this.id;
    }

}