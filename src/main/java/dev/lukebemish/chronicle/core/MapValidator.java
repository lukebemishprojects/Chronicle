package dev.lukebemish.chronicle.core;

public sealed interface MapValidator<T extends ChronicleMap> extends Validator<T> permits MapView {
    default void validate(T map) {

    }
}
