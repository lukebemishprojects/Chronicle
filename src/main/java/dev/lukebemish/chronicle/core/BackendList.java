package dev.lukebemish.chronicle.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BackendList implements Iterable<Object> {
    private final List<Object> values = new ArrayList<>();
    private final ChronicleContext context;

    public BackendList(ChronicleContext context) {
        this.context = context;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {;
            private final Iterator<Object> backendIterator = values.iterator();

            @Override
            public boolean hasNext() {
                return backendIterator.hasNext();
            }

            @Override
            public Object next() {
                return context.wrap(backendIterator.next());
            }
        };
    }

    public List<Object> convert() {
        List<Object> list = new ArrayList<>();
        for (Object item : values) {
            list.add(Utils.unBackendify(item));
        }
        return list;
    }

    public ChronicleContext context() {
        return context;
    }

    public int size() {
        return values.size();
    }

    public Object remove(int i) {
        return context.wrap(values.remove(i));
    }

    public void add(Object value) {
        values.add(Utils.backendify(value, context));
    }

    public void add(boolean value) {
        values.add(value);
    }

    public Object get(int i) {
        return context.wrap(values.get(i));
    }

    public void putAt(int i, Object value) {
        values.set(i, Utils.backendify(value, context));
    }

    public <T extends ChronicleMap> void add(Action<T> action, Class<T> viewClass) {
        BackendMap backendMap = new BackendMap(this.context);
        var view = context.mapView(viewClass);
        T wrapped = view.wrap(backendMap);
        action.call(wrapped);
        view.validate(backendMap);
        values.add(backendMap);
    }

    public <T extends ChronicleMap> void configure(int index, Action<T> action, Class<T> viewClass) {
        var existing = Utils.unwrap(get(index));
        BackendMap map;
        if (existing instanceof BackendMap existingMap) {
            map = existingMap;
        } else {
            throw new IllegalStateException("Cannot enter index " + index + " because it is already set to a non-map value");
        }
        var view = context.mapView(viewClass);
        T wrapped = view.wrap(map);
        action.call(wrapped);
        view.validate(map);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
