package dev.lukebemish.chronicle.core;

public sealed interface View<T> extends Validator<T> permits MapView, ListView {
}
