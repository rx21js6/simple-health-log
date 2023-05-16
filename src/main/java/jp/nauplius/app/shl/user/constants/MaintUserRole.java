package jp.nauplius.app.shl.user.constants;

/**
 * ユーザロール
 */
public enum MaintUserRole {
    ADMIN("ADMIN"), USER("USER"),;

    private final String text;

    private MaintUserRole(final String text) {
        this.text = text;
    }

    public String getString() {
        return this.text;
    }
}
