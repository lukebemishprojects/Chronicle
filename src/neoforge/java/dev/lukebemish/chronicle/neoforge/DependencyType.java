package dev.lukebemish.chronicle.neoforge;

public enum DependencyType {
    REQUIRED("required"), OPTIONAL("optional"),
    INCOMPATIBLE("incompatible"), DISCOURAGED("discouraged");

    public String getValue() {
        return this.value;
    }

    private final String value;

    DependencyType(String value) {
        this.value = value;
    }

    public static DependencyType forValue(String value) {
        for (DependencyType env : values()) {
            if (env.value.equals(value)) {
                return env;
            }
        }
        throw new IllegalArgumentException("Unknown dependency type: " + value);
    }
}
