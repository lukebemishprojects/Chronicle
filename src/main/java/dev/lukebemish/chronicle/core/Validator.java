package dev.lukebemish.chronicle.core;

public sealed interface Validator<T> permits ListValidator, MapValidator, View {
}
