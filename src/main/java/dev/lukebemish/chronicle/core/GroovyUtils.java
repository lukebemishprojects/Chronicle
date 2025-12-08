package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ConversionHandler;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Proxy;

final class GroovyUtils {
    private GroovyUtils() {}

    public static <T> void setDelegate(T value, Action<T> action) {
        if (action instanceof Proxy proxy) {
            try {
                var handler = Proxy.getInvocationHandler(proxy);
                if (handler instanceof ConversionHandler conversionHandler) {
                    if (conversionHandler.getDelegate() instanceof Closure<?> closure) {
                        closure.setDelegate(value);
                        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // Not a dynamic proxy, ignore
            }
        }
    }

    public static <T> @Nullable Action<T> tryAdaptClosure(@Nullable Object obj) {
        if (obj instanceof Closure<?> closure) {
            return adaptClosure(closure);
        }
        return null;
    }

    public static <T> Action<T> adaptClosure(Closure<?> closure) {
        return t -> {
            closure.setDelegate(t);
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call(t);
        };
    }
}
