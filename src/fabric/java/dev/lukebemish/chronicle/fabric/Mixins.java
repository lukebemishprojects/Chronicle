package dev.lukebemish.chronicle.fabric;

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
        this.backend().add(action, Mixin.VIEW);
    }
}
