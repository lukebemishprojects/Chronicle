package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ChronicleEngine<T> {
    private final View<T> view;

    public ChronicleEngine(View<T> view) {
        this.view = view;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object execute(@DelegatesTo(type = "T", strategy = Closure.DELEGATE_FIRST) Action<T> action) {
        return switch (view) {
            case MapView<?> mapView -> executeMap(mapView, (Action) action);
            case ListView<?> listView -> executeList(listView, (Action) action);
        };
    }

    private <R extends ChronicleList> Object executeList(ListView<R> listView, Action<R> action) {
        var backend = new BackendList();
        var dsl = listView.wrap(backend);
        action.call(dsl);
        listView.validate(backend);
        return backend.convert();
    }

    private <R extends ChronicleMap> Object executeMap(MapView<R> mapView, Action<R> action) {
        var backend = new BackendMap();
        var dsl = mapView.wrap(backend);
        action.call(dsl);
        mapView.validate(backend);
        return backend.convert();
    }
}
