package dev.lukebemish.chronicle.core;

public non-sealed interface ListView<T extends ChronicleList> extends View<T>, ListValidator<T> {
    T wrap(BackendList list);
}
