package dev.lukebemish.chronicle.core;

public non-sealed interface MapMixin<T extends ChronicleMap> extends DslMixin<T> {
    static BackendMap backend(MapMixin<?> instance) {
        return ((ChronicleMap) instance).backend();
    }
}
