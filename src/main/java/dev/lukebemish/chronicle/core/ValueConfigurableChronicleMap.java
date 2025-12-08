package dev.lukebemish.chronicle.core;

import java.util.function.BiConsumer;

public abstract class ValueConfigurableChronicleMap<T, R> extends ConfigurableChronicleMap<T> {
    protected ValueConfigurableChronicleMap(BackendMap backend) {
        super(backend);
    }

    protected abstract void valueConsumer(T entry, R value);

    protected void configure(String key, R value) {
        configure(key, entry -> valueConsumer(entry, value));
    }
}
