package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Mixin extends ChronicleMap {
    public Mixin(BackendMap backend) {
        super(backend);
    }

    public String getConfig() {
        return (String) Objects.requireNonNull(backend().get("config"));
    }

    public void setConfig(String config) {
        backend().putAt("config", config);
    }

    public @Nullable String getBehaviorVersion() {
        return (String) backend().get("behaviorVersion");
    }

    public void setBehaviorVersion(@Nullable String behaviorVersion) {
        backend().putAt("behaviorVersion", behaviorVersion);
    }

    public void requiredMods(@DelegatesTo(value = MixinRequiredMods.class, strategy = Closure.DELEGATE_FIRST) Action<MixinRequiredMods> action) {
        backend().configureList("requiredMods", action, MixinRequiredMods.class);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("config") instanceof String)) {
            throw new IllegalStateException("Expected 'config' to be present and a String");
        }
    }
}
