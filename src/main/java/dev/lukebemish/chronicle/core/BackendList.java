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

    public <T extends ChronicleMap> void add(Action<T> action, Class<T> viewClass, boolean validate) {
        BackendMap backendMap = new BackendMap(this.context);
        var view = context.mapView(viewClass);
        T wrapped = view.wrap(backendMap);
        action.call(wrapped);
        if (validate) {
            view.validate(wrapped);
        }
        values.add(backendMap);
    }

    public <T extends ChronicleList> void addList(Action<T> action, Class<T> viewClass, boolean validate) {
        BackendList backendList = new BackendList(this.context);
        var view = context.listView(viewClass);
        T wrapped = view.wrap(backendList);
        action.call(wrapped);
        if (validate) {
            view.validate(wrapped);
        }
        values.add(backendList);
    }

    public <T extends ChronicleMap> void configure(int index, Action<T> action, Class<T> viewClass, boolean validate) {
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
        if (validate) {
            view.validate(wrapped);
        }
    }

    public <T extends ChronicleList> void configureList(int index, Action<T> action, Class<T> viewClass, boolean validate) {
        var existing = Utils.unwrap(get(index));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else {
            throw new IllegalStateException("Cannot enter index " + index + " because it is already set to a non-list value");
        }
        var view = context.listView(viewClass);
        T wrapped = view.wrap(list);
        action.call(wrapped);
        if (validate) {
            view.validate(wrapped);
        }
    }

    public <T extends ChronicleMap> T get(int index, Class<T> viewClass) {
        var existing = Utils.unwrap(get(index));
        BackendMap map;
        if (existing instanceof BackendMap existingMap) {
            map = existingMap;
        } else {
            throw new IllegalStateException("Cannot get index " + index + " because it is already set to a non-map value");
        }
        var view = context.mapView(viewClass);
        return view.wrap(map);
    }

    public <T extends ChronicleList> T getList(int index, Class<T> viewClass) {
        var existing = Utils.unwrap(get(index));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else {
            throw new IllegalStateException("Cannot get index " + index + " because it is already set to a non-list value");
        }
        var view = context.listView(viewClass);
        return view.wrap(list);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
