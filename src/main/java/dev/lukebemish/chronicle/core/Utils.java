package dev.lukebemish.chronicle.core;

import org.jetbrains.annotations.Contract;
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

    @Contract("null -> null; !null -> !null")
    static @Nullable Object wrap(@Nullable Object value) {
        return switch (value) {
            case BackendMap map -> new GenericChronicleMap(map);
            case BackendList list -> new GenericChronicleList(list);
            case null -> null;
            default -> value;
        };
    }

    static Object backendify(Object value) {
        return switch (value) {
            case ChronicleMap map -> backendify(map.backend.convert());
            case ChronicleList list -> backendify(list.backend.convert());
            case Number n -> n;
            case String s -> s;
            case Boolean b -> b;
            case List<?> list -> {
                BackendList backendList = new BackendList();
                for (Object item : list) {
                    backendList.add(backendify(item));
                }
                yield backendList;
            }
            case Map<?, ?> map -> {
                BackendMap backendMap = new BackendMap();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    backendMap.set(entry.getKey().toString(), backendify(entry.getValue()));
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
            GroovyUtils.setDelegate(value, action);
        }
        action.invoke(value);
    }
}
