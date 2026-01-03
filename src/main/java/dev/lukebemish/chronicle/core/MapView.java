package dev.lukebemish.chronicle.core;

public non-sealed interface MapView<T extends ChronicleMap> extends View<T>, MapValidator<T> {
    T wrap(BackendMap map);
}
