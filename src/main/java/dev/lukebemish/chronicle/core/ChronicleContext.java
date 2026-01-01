package dev.lukebemish.chronicle.core;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ChronicleContext {
    private final ChronicleEngine<?> engine;
    private final Map<ContextKey<?>, Object> contextData = new IdentityHashMap<>();

    ChronicleContext(ChronicleEngine<?> engine) {
        this.engine = engine;
    }

    public <T> View<T> view(Class<T> clazz) {
        return engine.view(clazz);
    }

    public <T extends ChronicleMap> MapView<T> mapView(Class<T> clazz) {
        return engine.mapView(clazz);
    }

    public <T extends ChronicleList> ListView<T> listView(Class<T> clazz) {
        return engine.listView(clazz);
    }

    @Contract("null -> null; !null -> !null")
    @Nullable Object wrap(@Nullable Object value) {
        return switch (value) {
            case BackendMap map -> mapView(GenericChronicleMap.class).wrap(map);
            case BackendList list -> listView(GenericChronicleList.class).wrap(list);
            case null -> null;
            default -> value;
        };
    }

    void setContextData(ContextKey<?> key, Object value) {
        contextData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getContextData(ContextKey<T> key) {
        return (T) contextData.get(key);
    }
}
