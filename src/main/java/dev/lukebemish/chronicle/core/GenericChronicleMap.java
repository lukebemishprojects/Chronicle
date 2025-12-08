package dev.lukebemish.chronicle.core;

import groovy.lang.DelegatesTo;

public class GenericChronicleMap extends ConfigurableChronicleMap<GenericChronicleMap> {
    public GenericChronicleMap(BackendMap backend) {
        super(backend);
    }

    @Override
    protected MapView<GenericChronicleMap> entriesView() {
        return GenericChronicleMap::new;
    }

    @Override
    public void configure(String key, @DelegatesTo(value = GenericChronicleMap.class) Action<GenericChronicleMap> action) {
        super.configure(key, action);
    }

    public void add(String key, @DelegatesTo(value = GenericChronicleMap.class) Action<GenericChronicleMap> action) {
        backend.add(key, action, GenericChronicleMap::new);
    }
}
