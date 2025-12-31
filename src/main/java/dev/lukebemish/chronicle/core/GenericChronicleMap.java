package dev.lukebemish.chronicle.core;

import groovy.lang.DelegatesTo;

public class GenericChronicleMap extends ConfigurableChronicleMap<GenericChronicleMap> {
    public GenericChronicleMap(BackendMap backend) {
        super(backend);
    }

    @Override
    public void configure(String key, @DelegatesTo(value = GenericChronicleMap.class) Action<GenericChronicleMap> action) {
        backend.configure(key, action, GenericChronicleMap.class);
    }

    public void add(String key, @DelegatesTo(value = GenericChronicleMap.class) Action<GenericChronicleMap> action) {
        backend.add(key, action, GenericChronicleMap.class);
    }
}
