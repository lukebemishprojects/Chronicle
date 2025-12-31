package test.generator.target;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Root extends ChronicleMap {
    public Root(BackendMap backend) {
        super(backend);
    }

    public void foo(@DelegatesTo(value = Foo.class, strategy = Closure.DELEGATE_FIRST) Action<Foo> action) {
        backend().configure("foo", action, Foo.class);
    }
}
