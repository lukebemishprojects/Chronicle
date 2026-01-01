package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.List;

public class Dependencies extends ChronicleMap {
    public Dependencies(BackendMap backend) {
        super(backend);
    }

    public void add(String key, @DelegatesTo(value = Dependency.class, strategy = Closure.DELEGATE_FIRST) Action<Dependency> action) {
        backend().configureList(key, action, Dependency.class);
    }

    public void add(String key, String value) {
        backend().putAt(key, value);
    }

    public void add(String key, List<String> value) {
        backend().putAt(key, value);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        for (var entry : map) {
            if (!FabricModJson.MOD_ID.asMatchPredicate().test(entry.key())) {
                throw new IllegalStateException("ID of dependency '" + entry.key() + "' is invalid; it must match the regex " + FabricModJson.MOD_ID.pattern());
            }
        }
    }
}
