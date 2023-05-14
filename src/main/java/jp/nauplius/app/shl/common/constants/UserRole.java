package jp.nauplius.app.shl.common.constants;

/**
 * 利用者権限
 */
public enum UserRole {
    // 管理者
    ADMIN(0),
    // 一般利用者
    USER(1),
    // 制限利用者
    RESTRICTED(2);

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