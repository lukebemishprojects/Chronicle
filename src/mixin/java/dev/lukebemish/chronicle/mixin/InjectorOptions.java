package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

public class InjectorOptions extends ChronicleMap {
    public InjectorOptions(BackendMap backend) {
        super(backend);
    }

    public @Nullable Number getDefaultRequire() {
        return (Number) backend().get("defaultRequire");
    }

    public void setDefaultRequire(@Nullable Number defaultRequire) {
        backend().putAt("defaultRequire", defaultRequire);
    }

    public @Nullable String getDefaultGroup() {
        return (String) backend().get("defaultGroup");
    }

    public void setDefaultGroup(@Nullable String defaultGroup) {
        backend().putAt("defaultGroup", defaultGroup);
    }

    public @Nullable String getNamespace() {
        return (String) backend().get("namespace");
    }

    public void setNamespace(@Nullable String namespace) {
        backend().putAt("namespace", namespace);
    }

    public @Nullable Number getMaxShiftBy() {
        return (Number) backend().get("maxShiftBy");
    }

    public void setMaxShiftBy(@Nullable Number maxShiftBy) {
        backend().putAt("maxShiftBy", maxShiftBy);
    }

    public void injectionPoints(@DelegatesTo(value = InjectionPoints.class, strategy = Closure.DELEGATE_FIRST) Action<InjectionPoints> action) {
        backend().configureList("injectionPoints", action, InjectionPoints.class);
    }

    public void dynamicSelectors(@DelegatesTo(value = DynamicSelectors.class, strategy = Closure.DELEGATE_FIRST) Action<DynamicSelectors> action) {
        backend().configureList("dynamicSelectors", action, DynamicSelectors.class);
    }
}
