package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MixinConfig extends ChronicleMap {
    public MixinConfig(BackendMap backend) {
        super(backend);
    }

    public @Nullable String getParent() {
        return (String) backend().get("parent");
    }

    public void setParent(@Nullable String parent) {
        backend().putAt("parent", parent);
    }

    public @Nullable String getTarget() {
        return (String) backend().get("target");
    }

    public void setTarget(@Nullable String target) {
        backend().putAt("target", target);
    }

    public @Nullable String getMinVersion() {
        return (String) backend().get("minVersion");
    }

    public void setMinVersion(@Nullable String minVersion) {
        backend().putAt("minVersion", minVersion);
    }

    public void requiredFeatures(@DelegatesTo(value = RequiredFeatures.class, strategy = Closure.DELEGATE_FIRST) Action<RequiredFeatures> action) {
        backend().configureList("requiredFeatures", action, RequiredFeatures.class, false);
    }

    @DslValidate("requiredFeatures")
    public RequiredFeatures getRequiredFeatures() {
        return backend().getOrCreateList("requiredFeatures", RequiredFeatures.class);
    }

    public @Nullable CompatibilityLevel getCompatibilityLevel() {
        String level = (String) backend().get("compatibilityLevel");
        if (level == null) {
            return null;
        }
        return dev.lukebemish.chronicle.mixin.CompatibilityLevel.forValue(level);
    }

    public void setCompatibilityLevel(@Nullable CompatibilityLevel compatibilityLevel) {
        backend().putAt("compatibilityLevel", compatibilityLevel != null ? compatibilityLevel.getValue() : null);
    }

    public @Nullable Boolean getRequired() {
        return (Boolean) backend().get("required");
    }

    public void setRequired(@Nullable Boolean required) {
        backend().putAt("required", required);
    }

    public @Nullable Number getPriority() {
        return (Number) backend().get("priority");
    }

    public void setPriority(@Nullable Number priority) {
        backend().putAt("priority", priority);
    }

    public @Nullable Number getMixinPriority() {
        return (Number) backend().get("mixinPriority");
    }

    public @Nullable String getPackage() {
        return (String) Objects.requireNonNull(backend().get("package"));
    }

    public void setPackage(@Nullable String pkg) {
        backend().putAt("package", pkg);
    }

    public void mixins(@DelegatesTo(value = MixinClasses.class, strategy = Closure.DELEGATE_FIRST) Action<MixinClasses> action) {
        backend().configureList("mixins", action, MixinClasses.class, false);
    }

    @DslValidate("mixins")
    public MixinClasses getMixins() {
        return backend().getOrCreateList("mixins", MixinClasses.class);
    }

    public void client(@DelegatesTo(value = MixinClasses.class, strategy = Closure.DELEGATE_FIRST) Action<MixinClasses> action) {
        backend().configureList("client", action, MixinClasses.class, false);
    }

    @DslValidate("client")
    public MixinClasses getClient() {
        return backend().getOrCreateList("client", MixinClasses.class);
    }

    public void server(@DelegatesTo(value = MixinClasses.class, strategy = Closure.DELEGATE_FIRST) Action<MixinClasses> action) {
        backend().configureList("server", action, MixinClasses.class, false);
    }

    @DslValidate("server")
    public MixinClasses getServer() {
        return backend().getOrCreateList("server", MixinClasses.class);
    }

    public @Nullable Boolean getSetSourceFile() {
        return (Boolean) backend().get("setSourceFile");
    }

    public void setSetSourceFile(@Nullable Boolean setSourceFile) {
        backend().putAt("setSourceFile", setSourceFile);
    }

    public @Nullable String getRefmap() {
        return (String) backend().get("refmap");
    }

    public void setRefmap(@Nullable String refmap) {
        backend().putAt("refmap", refmap);
    }

    public @Nullable String getRefmapWrapper() {
        return (String) backend().get("refmapWrapper");
    }

    public void setRefmapWrapper(@Nullable String refmapWrapper) {
        backend().putAt("refmapWrapper", refmapWrapper);
    }

    public @Nullable Boolean getVerbose() {
        return (Boolean) backend().get("verbose");
    }

    public void setVerbose(@Nullable Boolean verbose) {
        backend().putAt("verbose", verbose);
    }

    public @Nullable String getPlugin() {
        return (String) backend().get("plugin");
    }

    public void setPlugin(@Nullable String plugin) {
        backend().putAt("plugin", plugin);
    }

    public void injectors(@DelegatesTo(value = InjectorOptions.class, strategy = Closure.DELEGATE_FIRST) Action<InjectorOptions> action) {
        backend().configure("injectors", action, InjectorOptions.class, false);
    }

    @DslValidate("injectors")
    public InjectorOptions getInjectors() {
        return backend().getOrCreate("injectors", InjectorOptions.class);
    }

    public void overwrites(@DelegatesTo(value = OverwriteOptions.class, strategy = Closure.DELEGATE_FIRST) Action<OverwriteOptions> action) {
        backend().configure("overwrites", action, OverwriteOptions.class, false);
    }

    @DslValidate("overwrites")
    public OverwriteOptions getOverwrites() {
        return backend().getOrCreate("overwrites", OverwriteOptions.class);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("minVersion") instanceof String) && (!(map.get("requiredFeatures") instanceof List<?> list) || list.isEmpty())) {
            throw new IllegalStateException("Expected either 'minVersion' or non-empty 'requiredFeatures' to be present");
        }
    }
}
