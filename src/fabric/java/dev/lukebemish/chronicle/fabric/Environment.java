package dev.lukebemish.chronicle.fabric;

public enum Environment {
    ANY("*"), CLIENT("client"), SERVER("server");

    public String getValue() {
        return this.value;
    }

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public static Environment forValue(String value) {
        for (Environment env : values()) {
            if (env.value.equals(value)) {
                return env;
            }
        }
        throw new IllegalArgumentException("Unknown environment: " + value);
    }
}
