package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class AccessTransformers extends ChronicleList {
    public AccessTransformers(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = AccessTransformer.class, strategy = Closure.DELEGATE_FIRST) Action<AccessTransformer> action) {
        backend().add(action, AccessTransformer.class, true);
    }
}
