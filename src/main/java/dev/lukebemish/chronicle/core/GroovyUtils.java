package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ConversionHandler;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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

    private static final MethodHandle EXECUTE_CLOSURE;

    static {
        try {
            var clazz = Class.forName("dev.lukebemish.chronicle.core.GroovyUtilsNative");
            EXECUTE_CLOSURE = MethodHandles.lookup().findStatic(clazz, "executeClosure", MethodType.methodType(void.class, Closure.class, Object.class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Action<T> adaptClosure(Closure<?> closure) {
        return t -> {
            try {
                EXECUTE_CLOSURE.invokeExact(closure, t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
