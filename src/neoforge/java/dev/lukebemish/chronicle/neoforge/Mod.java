package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import dev.lukebemish.chronicle.core.GenericChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

public class Mod extends ChronicleMap {
    public Mod(BackendMap backend) {
        super(backend);
    }

    public String getModId() {
        return (String) Objects.requireNonNull(backend().get("modId"));
    }

    public void setModId(String modId) {
        backend().putAt("modId", modId);
    }

    public String getVersion() {
        return (String) Objects.requireNonNull(backend().get("version"));
    }

    public void setVersion(String version) {
        backend().putAt("version", version);
    }

    public @Nullable String getDisplayName() {
        return (String) backend().get("displayName");
    }

    public void setDisplayName(@Nullable String displayName) {
        backend().putAt("displayName", displayName);
    }

    public @Nullable String getNamespace() {
        return (String) backend().get("namespace");
    }

    public void setNamespace(@Nullable String namespace) {
        backend().putAt("namespace", namespace);
    }

    public @Nullable String getDescription() {
        return (String) backend().get("description");
    }

    public void setDescription(@Nullable String description) {
        backend().putAt("description", description);
    }

    public @Nullable String getLogoFile() {
        return (String) backend().get("logoFile");
    }

    public void setLogoFile(@Nullable String logoFile) {
        backend().putAt("logoFile", logoFile);
    }

    public @Nullable Boolean getLogoBlur() {
        return (Boolean) backend().get("logoBlur");
    }

    public void setLogoBlur(@Nullable Boolean logoBlur) {
        backend().putAt("logoBlur", logoBlur);
    }

    public @Nullable String getUpdateJsonUrl() {
        return (String) backend().get("updateJSONURL");
    }

    public void setUpdateJsonUrl(@Nullable String updateJSONURL) {
        backend().putAt("updateJSONURL", updateJSONURL);
    }

    public void features(@DelegatesTo(value = Features.class, strategy = Closure.DELEGATE_ONLY) Action<Features> action) {
        backend().configure("features", action, Features.class, false);
    }

    @DslValidate("features")
    public Features getFeatures() {
        return backend().getOrCreate("features", Features.class);
    }

    public void modProperties(@DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_ONLY) Action<GenericChronicleMap> action) {
        backend().configure("modproperties", action, GenericChronicleMap.class, false);
    }

    @DslValidate("modproperties")
    public GenericChronicleMap getModProperties() {
        return backend().getOrCreate("modproperties", GenericChronicleMap.class);
    }

    public @Nullable String getModUrl() {
        return (String) backend().get("modUrl");
    }

    public void setModUrl(@Nullable String modUrl) {
        backend().putAt("modUrl", modUrl);
    }

    public @Nullable String getCredits() {
        return (String) backend().get("credits");
    }

    public void setCredits(@Nullable String credits) {
        backend().putAt("credits", credits);
    }

    public @Nullable String getAuthors() {
        return (String) backend().get("authors");
    }

    public void setAuthors(@Nullable String authors) {
        backend().putAt("authors", authors);
    }

    public @Nullable String getEnumExtensions() {
        return (String) backend().get("enumExtensions");
    }

    public void setEnumExtensions(@Nullable String enumExtensions) {
        backend().putAt("enumExtensions", enumExtensions);
    }

    public @Nullable String getFeatureFlags() {
        return (String) backend().get("featureFlags");
    }

    public void setFeatureFlags(@Nullable String featureFlags) {
        backend().putAt("featureFlags", featureFlags);
    }

    public void dependencies(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_ONLY) Action<Dependencies> action) {
        backend().configureList("dependencies", action, Dependencies.class, false);
    }

    @DslValidate("dependencies")
    public Dependencies getDependencies() {
        return backend().getOrCreateList("dependencies", Dependencies.class);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("modId") instanceof String string)) {
            throw new IllegalStateException("Expected 'id' to be present and a String");
        }
        if (!MOD_ID.asMatchPredicate().test(string)) {
            throw new IllegalStateException("Mod ID '" + string + "' is invalid; it must match the regex " + MOD_ID.pattern());
        }
        var namespace = map.get("namespace");
        if (namespace != null) {
            if (!(namespace instanceof String nsString)) {
                throw new IllegalStateException("Expected 'namespace' to be a String if present");
            }
            if (!NAMESPACE.asMatchPredicate().test(nsString)) {
                throw new IllegalStateException("Namespace '" + nsString + "' is invalid; it must match the regex " + NAMESPACE.pattern());
            }
        }
        var logoFile = map.get("logoFile");
        if (logoFile != null) {
            if (!(logoFile instanceof String logoString)) {
                throw new IllegalStateException("Expected 'logoFile' to be a String if present");
            }
            // TODO: is this predicate actually enforced anywhere in neo? Comes from the docs but may not be enforced.
            if (!LOGO_FILE.asMatchPredicate().test(logoString)) {
                throw new IllegalStateException("Logo file name '" + logoString + "' is invalid; it must match the regex " + LOGO_FILE.pattern());
            }
        }
        var version = map.get("version");
        if (version != null) {
            if (!(version instanceof String versionString)) {
                throw new IllegalStateException("Expected 'version' to be a String if present");
            }
            if (!VERSION.asMatchPredicate().test(versionString)) {
                throw new IllegalStateException("Version '" + versionString + "' is invalid; it must match the regex " + VERSION.pattern());
            }
        }
    }

    static final Pattern LOGO_FILE = Pattern.compile("^[a-z0-9_\\-.]+$");
    static final Pattern MOD_ID = Pattern.compile("^(?=.{2,64}$)[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$");
    static final Pattern NAMESPACE = Pattern.compile("^[a-z][a-z0-9_.-]{1,63}$");
    static final Pattern VERSION = Pattern.compile("^\\d+.*");
}
