package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Mixin extends ChronicleMap {
    public Mixin(BackendMap backend) {
        super(backend);
    }

    public String getConfig() {
        return (String) Objects.requireNonNull(this.get("config"));
    }

    public void setConfig(String config) {
        this.putAt("config", config);
    }

    public @Nullable Environment getEnvironment() {
        String env = (String) get("environment");
        if (env == null) {
            return null;
        }
        return Environment.forValue(env);
    }

    public void setEnvironment(@Nullable Environment environment) {
        backend().putAt("environment", environment instanceof Environment e ? e.getValue() : null);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("config") instanceof String)) {
            throw new IllegalStateException("Expected 'config' to be present and a String");
        }
    }
}
