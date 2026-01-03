package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Mods extends ChronicleList {
    public Mods(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = Mod.class, strategy = Closure.DELEGATE_ONLY) Action<Mod> action) {
        backend().add(action, Mod.class, true);
    }
}
