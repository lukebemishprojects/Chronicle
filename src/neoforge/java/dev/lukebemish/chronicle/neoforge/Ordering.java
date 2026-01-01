package dev.lukebemish.chronicle.neoforge;

public enum Ordering {
    BEFORE("before"), AFTER("after"), NONE("none");

    public String getValue() {
        return this.value;
    }

    private final String value;

    Ordering(String value) {
        this.value = value;
    }

    public static Ordering forValue(String value) {
        for (Ordering ord : values()) {
            if (ord.value.equals(value)) {
                return ord;
            }
        }
        throw new IllegalArgumentException("Unknown ordering: " + value);
    }
}
