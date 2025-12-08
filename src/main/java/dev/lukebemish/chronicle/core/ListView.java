package dev.lukebemish.chronicle.core;

public non-sealed interface ListView<T extends ChronicleList> extends View<T> {
    T wrap(BackendList list);
    default void validate(BackendList list) {

    }
}
