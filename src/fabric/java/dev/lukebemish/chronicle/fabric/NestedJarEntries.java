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

    public void jar(@DelegatesTo(value = NestedJarEntry.class, strategy = Closure.DELEGATE_FIRST) Action<NestedJarEntry> action) {
        backend().add(action, NestedJarEntry::new);
    }

    public void jar(String file) {
        backend().add(entry -> entry.setFile(file), NestedJarEntry::new);
    }
}
