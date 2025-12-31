package dev.lukebemish.chronicle.core;

public non-sealed interface ListMixin<T extends ChronicleList> extends DslMixin<T> {
    static BackendList backend(ListMixin<?> instance) {
        return ((ChronicleList) instance).backend();
    }
}
