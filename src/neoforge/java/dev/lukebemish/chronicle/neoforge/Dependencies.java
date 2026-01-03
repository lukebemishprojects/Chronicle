package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Dependencies extends ChronicleList {
    public Dependencies(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = Dependency.class, strategy = Closure.DELEGATE_ONLY) Action<Dependency> action) {
        backend().add(action, Dependency.class, true);
    }
}
