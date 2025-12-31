package dev.lukebemish.chronicle.core;

import org.jetbrains.annotations.ApiStatus;

public interface ActionLike<T> {
    @ApiStatus.NonExtendable
    default void call(T value) {
        Utils.invokeAction(value, (Action<T>) this);
    }

    @ApiStatus.NonExtendable
    default Action<T> then(Action<? super T> action) {
        return t -> {
            this.call(t);
            action.call(t);
        };
    }

    default <R extends T> Action<R> narrow() {
        return this::call;
    }
}
