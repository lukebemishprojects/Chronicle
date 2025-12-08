package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import kotlin.jvm.JvmName;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ChronicleMap implements Iterable<ChronicleMap.Entry> {
    final BackendMap backend;

    protected ChronicleMap(BackendMap backend) {
        this.backend = backend;
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

    public void set(String key, List<?> value) {
        backend.set(key, value);
    }

    public void set(String key, Map<?, ?> value) {
        backend.set(key, value);
    }

    public void set(String key, String value) {
        backend.set(key, value);
    }

    public void set(String key, Number value) {
        backend.set(key, value);
    }

    public void set(String key, boolean value) {
        backend.set(key, value);
    }

    @Override
    public String toString() {
        return backend.toString();
    }

    public record Entry(String key, Object value) {}
}
