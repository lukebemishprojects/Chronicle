package dev.lukebemish.chronicle.core;

import kotlin.jvm.JvmName;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ChronicleMap implements Iterable<ChronicleMap.Entry> {
    final BackendMap backend;
    private final MapValidator<?> validator;

    public ChronicleMap(BackendMap backend) {
        this.backend = backend;
        this.validator = backend.context().mapView(getClass());
    }

    @SuppressWarnings("unchecked")
    protected static <T extends ChronicleMap> void validate(T map) {
        var validator = (MapValidator<T>) ((ChronicleMap) map).validator;
        validator.validate(map);
    }

    protected static <T extends ChronicleList> void validate(T list) {
        ChronicleList.validate(list);
    }

    protected static BackendMap backend(ChronicleMap map) {
        return map.backend;
    }

    protected static BackendList backend(ChronicleList list) {
        return ChronicleList.backend(list);
    }

    protected BackendMap backend() {
        return backend;
    }

    @Override
    public Iterator<Entry> iterator() {
        return backend.iterator();
    }

    @JvmName(name = "notAValidName")
    public @Nullable Object get(String key) {
        return backend.get(key);
    }

    public @Nullable Object remove(String key) {
        return backend.remove(key);
    }

    public int size() {
        return backend.size();
    }

    public void putAt(String key, List<?> value) {
        backend.putAt(key, value);
    }

    public void putAt(String key, Map<?, ?> value) {
        backend.putAt(key, value);
    }

    public void putAt(String key, String value) {
        backend.putAt(key, value);
    }

    public void putAt(String key, Number value) {
        backend.putAt(key, value);
    }

    public void putAt(String key, boolean value) {
        backend.putAt(key, value);
    }

    public void propertyMissing(String key, Object value) {
        backend.putAt(key, value);
    }

    @Override
    public String toString() {
        return backend.toString();
    }

    public record Entry(String key, Object value) {}
}
