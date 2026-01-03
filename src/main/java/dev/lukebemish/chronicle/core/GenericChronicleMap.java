package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class GenericChronicleMap extends ConfigurableChronicleMap<GenericChronicleMap> {
    public GenericChronicleMap(BackendMap backend) {
        super(backend);
    }

    @Override
    public void configure(String key, @DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_ONLY) Action<GenericChronicleMap> action) {
        backend.configure(key, action, GenericChronicleMap.class, true);
    }

    public void add(String key, @DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_ONLY) Action<GenericChronicleMap> action) {
        backend.add(key, action, GenericChronicleMap.class, true);
    }
}
