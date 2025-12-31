package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.List;

public class Dependencies extends ChronicleMap {
    public Dependencies(BackendMap backend) {
        super(backend);
    }

    public void mod(String key, @DelegatesTo(value = Dependency.class, strategy = Closure.DELEGATE_FIRST) Action<Dependency> action) {
        validateId(key);
        backend().configureList(key, action, Dependency.class);
    }

    public void mod(String key, String value) {
        validateId(key);
        backend().set(key, value);
    }

    public void mod(String key, List<String> value) {
        validateId(key);
        backend().set(key, value);
    }

    private void validateId(String id) {
        if (!FabricModJson.MOD_ID.asMatchPredicate().test(id)) {
            throw new IllegalStateException("ID of dependency '" + id + "' is invalid; it must match the regex " + FabricModJson.MOD_ID.pattern());
        }
    }
}
