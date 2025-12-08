package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ListView;
import dev.lukebemish.chronicle.core.ValueConfigurableChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.List;

public class Dependencies extends ValueConfigurableChronicleMap<Dependency, String> {
    protected Dependencies(BackendMap backend) {
        super(backend);
    }

    @Override
    protected void valueConsumer(Dependency entry, String value) {
        entry.add(value);
    }

    @Override
    protected ListView<Dependency> entriesView() {
        return Dependency::new;
    }

    public void mod(String key, @DelegatesTo(value = Dependency.class, strategy = Closure.DELEGATE_FIRST) Action<Dependency> action) {
        this.configure(key, action);
    }

    public void mod(String key, String value) {
        this.configure(key, value);
    }

    public void mod(String key, List<String> value) {
        validateId(key);
        backend().set(key, value);
    }

    @Override
    protected void configure(String key, Action<Dependency> action) {
        validateId(key);
        backend().configureList(key, action, entriesView());
    }

    @Override
    protected void configure(String key, String value) {
        validateId(key);
        backend().set(key, value);
    }

    private void validateId(String id) {
        if (FabricModJson.MOD_ID.asMatchPredicate().test(id)) {
            throw new IllegalStateException("ID of dependency '" + id + "' is invalid; it must match the regex " + FabricModJson.MOD_ID.pattern());
        }
    }

    // Groovy compatibility

    @SuppressWarnings("unchecked")
    @Override
    protected boolean methodMissingImpl(String name, Object args) {
        if (args instanceof Object[] argsArray && argsArray.length == 1 && argsArray[0] instanceof List<?> list) {
            this.mod(name, (List<String>) list);
            return true;
        }
        return super.methodMissingImpl(name, args);
    }
}
