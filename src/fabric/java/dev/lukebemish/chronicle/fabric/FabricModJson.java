package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class FabricModJson extends ChronicleMap {
    public FabricModJson(BackendMap backend) {
        super(backend);
        backend.set("schemaVersion", 1);
    }

    public String getId() {
        return (String) Objects.requireNonNull(get("id"));
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getVersion() {
        return (String) Objects.requireNonNull(get("version"));
    }

    public void setVersion(String version) {
        set("version", version);
    }

    public @Nullable Environment getEnvironment() {
        String env = (String) get("environment");
        if (env == null) {
            return null;
        }
        for (Environment environment : Environment.values()) {
            if (environment.value.equals(env)) {
                return environment;
            }
        }
        throw new IllegalStateException("Unknown environment: " + env);
    }

    public void setEnvironment(@Nullable Environment environment) {
        backend().set("environment", environment instanceof Environment e ? e.value : null);
    }

    public enum Environment {
        ANY("*"), CLIENT("client"), SERVER("server");

        private final String value;

        Environment(String value) {
            this.value = value;
        }
    }

    public @Nullable String getName() {
        return (String) get("name");
    }

    public void setName(@Nullable String name) {
        backend().set("name", name);
    }

    public @Nullable String getDescription() {
        return (String) get("description");
    }

    public void setDescription(@Nullable String description) {
        backend().set("description", description);
    }

    public void jars(@DelegatesTo(value = NestedJarEntries.class, strategy = Closure.DELEGATE_FIRST) Action<NestedJarEntries> action) {
        backend().configureList("jars", action, NestedJarEntries::new);
    }

    public void entrypoints(@DelegatesTo(value = EntrypointContainer.class, strategy = Closure.DELEGATE_FIRST) Action<EntrypointContainer> action) {
        backend().configure("entrypoints", action, EntrypointContainer::new);
    }
}
