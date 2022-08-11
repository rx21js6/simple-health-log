package jp.nauplius.app.shl.common.constants;

public enum UserRole {
    ADMIN(0), NORMAL(1), RESTRICTED(2);

    private final int id;

    private UserRole(final int id) {
        this.id = id;
    }

    public int getInt() {
        return this.id;
    }

    public static UserRole valueOf(int id) {
        UserRole[] userRoles = UserRole.values();
        for (UserRole userRole : userRoles) {
            if (userRole.getInt() == id) {
                return userRole;
            }
        }
        return null;
    }
}