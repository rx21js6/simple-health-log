package jp.nauplius.app.shl.user.constants;

public enum UserRoleId {
    ADMIN(0), NORMAL(1), RESTRICTED(2);

    private int roleId;

    UserRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getInt() {
        return this.roleId;
    }
}
