package dev.lukebemish.chronicle.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ChronicleList implements Iterable<Object> {
    final BackendList backend;
    private final ListValidator<?> validator;

    public ChronicleList(BackendList backend) {
        this.backend = backend;
        this.validator = backend.context().listView(getClass());
    }

    @SuppressWarnings("unchecked")
    protected static <T extends ChronicleList> void validate(T list) {
        var validator = (ListValidator<T>) ((ChronicleList) list).validator;
        validator.validate(list);
    }

    protected static <T extends ChronicleMap> void validate(T map) {
        ChronicleMap.validate(map);
    }

    protected static BackendMap backend(ChronicleMap map) {
        return ChronicleMap.backend(map);
    }

    protected static BackendList backend(ChronicleList list) {
        return list.backend;
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

    public void putAt(int i, List<?> value) {
        backend.putAt(i, value);
    }

    public void putAt(int i, Map<?, ?> value) {
        backend.putAt(i, value);
    }

    public void putAt(int i, String value) {
        backend.putAt(i, value);
    }

    public void putAt(int i, Number value) {
        backend.putAt(i, value);
    }

    public void putAt(int i, boolean value) {
        backend.putAt(i, value);
    }

    @Override
    public String toString() {
        return backend.toString();
    }
}
