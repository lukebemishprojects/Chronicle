package test.generator.target;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Foo extends ChronicleMap {
    public Foo(BackendMap backend) {
        super(backend);
    }

    public void bar(@DelegatesTo(value = Bar.class, strategy = Closure.DELEGATE_ONLY) Action<Bar> action) {
        backend().configure("bar", action, Bar.class, true);
    }
}
