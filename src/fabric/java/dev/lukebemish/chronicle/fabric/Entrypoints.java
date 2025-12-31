package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.GenericChronicleList;
import dev.lukebemish.chronicle.core.ValueConfigurableChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class Entrypoints extends ValueConfigurableChronicleMap<Entrypoint, String> {
    public Entrypoints(BackendMap backend) {
        super(backend);
    }

    public void entrypoint(String key, @DelegatesTo(value = Entrypoint.class, strategy = Closure.DELEGATE_FIRST) Action<Entrypoint> action) {
        this.configure(key, action);
    }

    public void entrypoint(String key, String value) {
        this.configure(key, value);
    }

    @Override
    protected void configure(String key, Action<Entrypoint> action) {
        backend().add(key, action, Entrypoint.class);
    }

    @Override
    protected void configure(String key, String value) {
        backend().configureList(key, list -> list.add(value), GenericChronicleList.class);
    }
}
