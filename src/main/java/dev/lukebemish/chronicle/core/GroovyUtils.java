package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ConversionHandler;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Proxy;

final class GroovyUtils {
    private GroovyUtils() {}

    public static <T> Action<T> rehydrate(Action<T> action) {
        if (action instanceof Proxy proxy) {
            try {
                var handler = Proxy.getInvocationHandler(proxy);
                if (handler instanceof ConversionHandler conversionHandler) {
                    if (conversionHandler.getDelegate() instanceof Closure<?> closure) {
                        return adaptClosure(closure);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // Not a dynamic proxy, ignore
            }
        }
        return action;
    }

    public static <T> @Nullable Action<T> tryAdaptClosure(@Nullable Object obj) {
        if (obj instanceof Closure<?> closure) {
            return adaptClosure(closure);
        }
        return null;
    }

    public static <T> Action<T> adaptClosure(Closure<?> closure) {
        return t -> {
            var rehydrated = closure.rehydrate(t, t, t);
            rehydrated.call(t);
        };
    }
}
