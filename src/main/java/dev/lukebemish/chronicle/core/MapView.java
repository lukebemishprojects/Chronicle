package dev.lukebemish.chronicle.core;

public non-sealed interface MapView<T extends ChronicleMap> extends View<T> {
    T wrap(BackendMap map);
    default void validate(BackendMap map) {

    }
}
