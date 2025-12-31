package dev.lukebemish.chronicle.core;

import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;

public final class BackendMap implements Iterable<ChronicleMap.Entry> {
    private final SequencedMap<String, Object> backend = new LinkedHashMap<>();
    private final ChronicleContext context;

    public BackendMap(ChronicleContext context) {
        this.context = context;
    }

    public Map<String, Object> convert() {
        var map = new LinkedHashMap<String, Object>();
        for (var entry : backend.entrySet()) {
            map.put(entry.getKey(), Utils.unBackendify(entry.getValue()));
        }
        return map;
    }

    public ChronicleContext context() {
        return context;
    }

    @Override
    public Iterator<ChronicleMap.Entry> iterator() {
        return new Iterator<>() {
            private final Iterator<Map.Entry<String, Object>> backingIterator = backend.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return backingIterator.hasNext();
            }

            @Override
            public ChronicleMap.Entry next() {
                return new ChronicleMap.Entry(
                    backingIterator.next().getKey(),
                    context.wrap(backingIterator.next().getValue())
                );
            }
        };
    }

    public @Nullable Object remove(String key) {
        return context.wrap(backend.remove(key));
    }

    public @Nullable Object get(String key) {
        return context.wrap(backend.get(key));
    }

    public int size() {
        return backend.size();
    }

    public void set(String key, @Nullable Object value) {
        if (value == null) {
            backend.remove(key);
        } else {
            backend.put(key, Utils.backendify(value, context));
        }
    }

    public <T extends ChronicleMap> void configure(String key, Action<T> action, Class<T> viewClass) {
        var existing = Utils.unwrap(get(key));
        BackendMap map;
        if (existing instanceof BackendMap existingMap) {
            map = existingMap;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot enter key '" + key + "' because it is already set to a non-map value");
        } else {
            map = new BackendMap(this.context);
            backend.put(key, map);
        }
        var view = context.mapView(viewClass);
        T wrapped = view.wrap(map);
        action.call(wrapped);
        view.validate(map);
    }

    public <T extends ChronicleList> void configureList(String key, Action<T> action, Class<T> viewClass) {
        var existing = Utils.unwrap(get(key));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot enter key '" + key + "' because it is already set to a non-list value");
        } else {
            list = new BackendList(this.context);
            backend.put(key, list);
        }
        var view = context.listView(viewClass);
        var wrapped = view.wrap(list);
        action.call(wrapped);
        view.validate(list);
    }

    public <T extends ChronicleMap> void add(String key, Action<T> mapAction, Class<T> viewClass) {
        var existing = Utils.unwrap(get(key));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot add to key '" + key + "' because it is already set to a non-list value");
        } else {
            list = new BackendList(this.context);
            backend.put(key, list);
        }
        list.add(mapAction, viewClass);
    }

    @Override
    public String toString() {
        return backend.toString();
    }
}
