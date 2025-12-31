package dev.lukebemish.chronicle.core;

public sealed interface DslMixin<T> permits ListMixin, MapMixin {
    @SuppressWarnings("unchecked")
    static <T> T instance(DslMixin<T> instance) {
        return (T) instance;
    }
}
