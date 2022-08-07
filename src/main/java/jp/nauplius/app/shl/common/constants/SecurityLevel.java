package jp.nauplius.app.shl.common.constants;

public enum SecurityLevel {
    LEVEL0(0), LEVEL1(1);

    private final int id;

    private SecurityLevel(final int id) {
        this.id = id;
    }

    public int getInt() {
        return this.id;
    }

}