package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.MapView;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Entrypoint extends ChronicleMap {
    private Entrypoint(BackendMap backend) {
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

    public static final MapView<Entrypoint> VIEW = new MapView<>() {
        @Override
        public Entrypoint wrap(BackendMap map) {
            return new Entrypoint(map);
        }

        @Override
        public void validate(BackendMap map) {
            if (!(map.get("value") instanceof String)) {
                throw new IllegalStateException("Expected 'value' to be present and a String");
            }
        }
    };
}
