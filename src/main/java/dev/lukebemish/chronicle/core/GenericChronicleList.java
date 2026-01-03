package dev.lukebemish.chronicle.core;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class GenericChronicleList extends ChronicleList {
    public GenericChronicleList(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_FIRST) Action<GenericChronicleMap> action) {
        backend.add(action, GenericChronicleMap.class, true);
    }

    public void configure(int index, @DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_FIRST) Action<GenericChronicleMap> action) {
        backend.configure(index, action, GenericChronicleMap.class, true);
    }
}
