package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class People extends ChronicleList {
    public People(BackendList backend) {
        super(backend);
    }

    public void add(@DelegatesTo(value = Person.class, strategy = Closure.DELEGATE_FIRST) Action<Person> action) {
        this.backend().add(action, Person.class);
    }
}
