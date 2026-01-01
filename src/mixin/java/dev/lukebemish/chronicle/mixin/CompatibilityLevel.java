package dev.lukebemish.chronicle.mixin;

public record CompatibilityLevel(int level) {
    public static final CompatibilityLevel JAVA_6 = new CompatibilityLevel(6);
    public static final CompatibilityLevel JAVA_7 = new CompatibilityLevel(7);
    public static final CompatibilityLevel JAVA_8 = new CompatibilityLevel(8);
    public static final CompatibilityLevel JAVA_9 = new CompatibilityLevel(9);
    public static final CompatibilityLevel JAVA_10 = new CompatibilityLevel(10);
    public static final CompatibilityLevel JAVA_11 = new CompatibilityLevel(11);
    public static final CompatibilityLevel JAVA_12 = new CompatibilityLevel(12);
    public static final CompatibilityLevel JAVA_13 = new CompatibilityLevel(13);
    public static final CompatibilityLevel JAVA_14 = new CompatibilityLevel(14);
    public static final CompatibilityLevel JAVA_15 = new CompatibilityLevel(15);
    public static final CompatibilityLevel JAVA_16 = new CompatibilityLevel(16);
    public static final CompatibilityLevel JAVA_17 = new CompatibilityLevel(17);
    public static final CompatibilityLevel JAVA_18 = new CompatibilityLevel(18);
    public static final CompatibilityLevel JAVA_19 = new CompatibilityLevel(19);
    public static final CompatibilityLevel JAVA_20 = new CompatibilityLevel(20);
    public static final CompatibilityLevel JAVA_21 = new CompatibilityLevel(21);
    public static final CompatibilityLevel JAVA_22 = new CompatibilityLevel(22);
    public static final CompatibilityLevel JAVA_23 = new CompatibilityLevel(23);
    public static final CompatibilityLevel JAVA_24 = new CompatibilityLevel(24);
    public static final CompatibilityLevel JAVA_25 = new CompatibilityLevel(25);

    public String getValue() {
        return "JAVA_" + level;
    }

    public static CompatibilityLevel forValue(String value) {
        if (value.startsWith("JAVA_")) {
            int lvl = Integer.parseInt(value.substring(5));
            return new CompatibilityLevel(lvl);
        }
        throw new IllegalArgumentException("Unknown compatibility level: " + value);
    }
}
