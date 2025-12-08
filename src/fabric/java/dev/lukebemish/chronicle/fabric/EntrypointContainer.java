package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.MapView;
import dev.lukebemish.chronicle.core.ValueConfigurableChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class EntrypointContainer extends ValueConfigurableChronicleMap<Entrypoint, String> {
    protected EntrypointContainer(BackendMap backend) {
        super(backend);
    }

    @Override
    protected void valueConsumer(Entrypoint entry, String value) {
        entry.setValue(value);
    }

    @Override
    protected MapView<Entrypoint> entriesView() {
        return Entrypoint::new;
    }

    public void entrypoint(String key, @DelegatesTo(value = Entrypoint.class, strategy = Closure.DELEGATE_FIRST) Action<Entrypoint> action) {
        this.configure(key, action);
    }

    public void entrypoint(String key, String value) {
        this.configure(key, value);
    }

    @Override
    protected void configure(String key, Action<Entrypoint> action) {
        backend().add(key, action, entriesView());
    }
}
