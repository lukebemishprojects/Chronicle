package dev.lukebemish.chronicle.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ChronicleList implements Iterable<Object> {
    final BackendList backend;

    protected ChronicleList(BackendList backend) {
        this.backend = backend;
    }

    protected BackendList backend() {
        return backend;
    }

    @Override
    public Iterator<Object> iterator() {
        return backend.iterator();
    }

    public int size() {
        return backend.size();
    }

    public Object remove(int i) {
        return backend.remove(i);
    }

    public void add(List<?> value) {
        backend.add(value);
    }

    public void add(Map<?, ?> value) {
        backend.add(value);
    }

    public void add(String value) {
        backend.add(value);
    }

    public void add(Number value) {
        backend.add(value);
    }

    public void add(boolean value) {
        backend.add(value);
    }

    public Object get(int i) {
        return backend.get(i);
    }

    public void set(int i, List<?> value) {
        backend.set(i, value);
    }

    public void set(int i, Map<?, ?> value) {
        backend.set(i, value);
    }

    public void set(int i, String value) {
        backend.set(i, value);
    }

    public void set(int i, Number value) {
        backend.set(i, value);
    }

    public void set(int i, boolean value) {
        backend.set(i, value);
    }

    @Override
    public String toString() {
        return backend.toString();
    }
}
