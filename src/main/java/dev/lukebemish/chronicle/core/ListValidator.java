package dev.lukebemish.chronicle.core;

public sealed interface ListValidator<T extends ChronicleList> extends Validator<T> permits ListView {
    default void validate(T list) {

    }
}
