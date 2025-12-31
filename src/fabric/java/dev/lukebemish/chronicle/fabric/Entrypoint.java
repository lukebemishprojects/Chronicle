package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Entrypoint extends ChronicleMap {
    public Entrypoint(BackendMap backend) {
        super(backend);
    }

    public String getAdapter() {
        return (String) Objects.requireNonNull(get("adapter"));
    }

    public void setAdapter(@Nullable String adapter) {
        backend().putAt("adapter", adapter);
    }

    public String getValue() {
        return (String) Objects.requireNonNull(get("value"));
    }

    public void setValue(String value) {
        backend().putAt("value", value);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("value") instanceof String)) {
            throw new IllegalStateException("Expected 'value' to be present and a String");
        }
    }
}
