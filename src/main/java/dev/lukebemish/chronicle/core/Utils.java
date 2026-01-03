package dev.lukebemish.chronicle.core;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

final class Utils {
    private Utils() {}

    static @Nullable Object unwrap(@Nullable Object value) {
        return switch (value) {
            case ChronicleMap map -> map.backend;
            case ChronicleList list -> list.backend;
            case null -> null;
            default -> value;
        };
    }

    static Object backendify(Object value, ChronicleContext context) {
        return switch (value) {
            case ChronicleMap map -> backendify(map.backend.convert(), context);
            case ChronicleList list -> backendify(list.backend.convert(), context);
            case Number n -> n;
            case String s -> s;
            case Boolean b -> b;
            case List<?> list -> {
                BackendList backendList = new BackendList(context);
                for (Object item : list) {
                    backendList.add(backendify(item, context));
                }
                yield backendList;
            }
            case Map<?, ?> map -> {
                BackendMap backendMap = new BackendMap(context);
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    backendMap.putAt(entry.getKey().toString(), backendify(entry.getValue(), context));
                }
                yield backendMap;
            }
            default -> throw new IllegalArgumentException("Cannot understand value "+value+" of type " + value.getClass().getName());
        };
    }

    static Object unBackendify(Object value) {
        return switch (value) {
            case BackendMap map -> map.convert();
            case BackendList list -> list.convert();
            case Number n -> n;
            case String s -> s;
            case Boolean b -> b;
            default -> throw new IllegalArgumentException("Cannot understand backend value "+value+" of type " + value.getClass().getName());
        };
    }

    static boolean isGroovyPresent() {
        try {
            Class.forName("groovy.lang.Closure");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static <T> void invokeAction(T value, Action<T> action) {
        if (isGroovyPresent()) {
            action = GroovyUtils.rehydrate(action);
        }
        action.invoke(value);
    }
}
