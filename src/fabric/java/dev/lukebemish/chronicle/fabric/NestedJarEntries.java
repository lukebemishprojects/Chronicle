package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class NestedJarEntries extends ChronicleList {
    public NestedJarEntries(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = NestedJarEntry.class, strategy = Closure.DELEGATE_ONLY) Action<NestedJarEntry> action) {
        backend().add(action, NestedJarEntry.class, true);
    }
}
