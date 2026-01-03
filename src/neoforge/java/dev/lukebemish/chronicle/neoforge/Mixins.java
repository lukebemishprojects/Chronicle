package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Mixins extends ChronicleList {
    public Mixins(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = Mixin.class, strategy = Closure.DELEGATE_FIRST) Action<Mixin> action) {
        backend().add(action, Mixin.class, true);
    }
}
