package dev.lukebemish.chronicle.core;

import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BackendMap implements Iterable<ChronicleMap.Entry> {
    private final Map<String, Object> backend = new LinkedHashMap<>();

    public Map<String, Object> convert() {
        var map = new LinkedHashMap<String, Object>();
        for (var entry : this) {
            map.put(entry.key(), Utils.unBackendify(entry.value()));
        }
        return map;
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
                    Utils.wrap(backingIterator.next().getValue())
                );
            }
        };
    }

    public @Nullable Object remove(String key) {
        return Utils.wrap(backend.remove(key));
    }

    public @Nullable Object get(String key) {
        return Utils.wrap(backend.get(key));
    }

    public int size() {
        return backend.size();
    }

    public void set(String key, @Nullable Object value) {
        if (value == null) {
            backend.remove(key);
        } else {
            backend.put(key, Utils.backendify(value));
        }
    }

    public <T extends ChronicleMap> void configure(String key, Action<T> action, MapView<T> view) {
        var existing = Utils.unwrap(get(key));
        BackendMap map;
        if (existing instanceof BackendMap existingMap) {
            map = existingMap;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot enter key '" + key + "' because it is already set to a non-map value");
        } else {
            map = new BackendMap();
            backend.put(key, map);
        }
        T wrapped = view.wrap(map);
        action.call(wrapped);
        view.validate(map);
    }

    public <T extends ChronicleList> void configureList(String key, Action<T> action, ListView<T> view) {
        var existing = Utils.unwrap(get(key));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot enter key '" + key + "' because it is already set to a non-list value");
        } else {
            list = new BackendList();
            backend.put(key, list);
        }
        var wrapped = view.wrap(list);
        action.call(wrapped);
        view.validate(list);
    }

    public <T extends ChronicleMap> void add(String key, Action<T> mapAction, MapView<T> mapView) {
        var existing = Utils.unwrap(get(key));
        BackendList list;
        if (existing instanceof BackendList existingList) {
            list = existingList;
        } else if (existing != null) {
            throw new IllegalStateException("Cannot add to key '" + key + "' because it is already set to a non-list value");
        } else {
            list = new BackendList();
            backend.put(key, list);
        }
        list.add(mapAction, mapView);
    }

    @Override
    public String toString() {
        return backend.toString();
    }
}
