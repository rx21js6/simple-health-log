package jp.nauplius.app.shl.common.constants;

/**
 * セキュリティレベル
 * 
 * @author developer
 *
 */
public enum SecurityLevel {
    // 旧
    LEVEL0(0),
    // 現
    LEVEL1(1);

    private final int id;

    private SecurityLevel(final int id) {
        this.id = id;
    }

    public int getInt() {
        return this.id;
    }

}