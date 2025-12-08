package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Entrypoint extends ChronicleMap {
    protected Entrypoint(BackendMap backend) {
        super(backend);
    }

    public String getAdapter() {
        return (String) Objects.requireNonNull(get("adapter"));
    }

    public void setAdapter(@Nullable String adapter) {
        backend().set("adapter", adapter);
    }

    public String getValue() {
        return (String) Objects.requireNonNull(get("value"));
    }

    public void setValue(String value) {
        backend().set("value", value);
    }
}
