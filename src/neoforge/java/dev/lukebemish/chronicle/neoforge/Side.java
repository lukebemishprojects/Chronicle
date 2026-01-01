package dev.lukebemish.chronicle.neoforge;

public enum Side {
    CLIENT("client"), SERVER("server"), BOTH("both");

    public String getValue() {
        return this.value;
    }

    private final String value;

    Side(String value) {
        this.value = value;
    }

    public static Side forValue(String value) {
        for (Side side : values()) {
            if (side.value.equals(value)) {
                return side;
            }
        }
        throw new IllegalArgumentException("Unknown side: " + value);
    }
}
