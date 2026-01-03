package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import dev.lukebemish.chronicle.core.GenericChronicleList;
import dev.lukebemish.chronicle.core.GenericChronicleMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class FabricModJson extends ChronicleMap {
    public FabricModJson(BackendMap backend) {
        super(backend);
        backend.putAt("schemaVersion", 1);
    }

    public String getId() {
        return (String) Objects.requireNonNull(get("id"));
    }

    public void setId(String id) {
        if (!MOD_ID.asMatchPredicate().test(id)) {
            throw new IllegalStateException("Mod ID '" + id + "' is invalid; it must match the regex " + MOD_ID.pattern());
        }
        putAt("id", id);
    }

    public String getVersion() {
        return (String) Objects.requireNonNull(get("version"));
    }

    public void setVersion(String version) {
        putAt("version", version);
    }

    public @Nullable Environment getEnvironment() {
        String env = (String) get("environment");
        if (env == null) {
            return null;
        }
        return Environment.forValue(env);
    }

    public void setEnvironment(@Nullable Environment environment) {
        backend().putAt("environment", environment instanceof Environment e ? e.getValue() : null);
    }

    public @Nullable String getName() {
        return (String) get("name");
    }

    public void setName(@Nullable String name) {
        backend().putAt("name", name);
    }

    public @Nullable String getDescription() {
        return (String) get("description");
    }

    public void setDescription(@Nullable String description) {
        backend().putAt("description", description);
    }

    @SuppressWarnings("DataFlowIssue")
    public @Nullable String getLicense() {
        var val = get("license");
        return switch (val) {
            case GenericChronicleList list when list.size() == 1 -> (String) list.get(0);
            case null -> null;
            default -> (String) val;
        };
    }

    public @Nullable GenericChronicleList getLicenses() {
        var val = get("license");
        return switch (val) {
            case String string -> {
                backend().putAt("license", List.of(string));
                yield (GenericChronicleList) backend().get("license");
            }
            case null -> null;
            default -> (GenericChronicleList) val;
        };
    }

    public void setLicense(@Nullable Object license) {
        backend().putAt("license", license);
    }

    public void setLicenses(@Nullable List<String> licenses) {
        backend().putAt("license", licenses);
    }

    public void license(String value) {
        var licenses = getLicenses();
        if (licenses == null) {
            setLicenses(List.of(value));
        } else {
            licenses.add(value);
        }
    }

    public void contact(@DelegatesTo(value = ContactInformation.class, strategy = Closure.DELEGATE_FIRST) Action<ContactInformation> action) {
        backend().configure("contact", action, ContactInformation.class, false);
    }

    @DslValidate("contact")
    public ContactInformation getContact() {
        return backend().getOrCreate("contact", ContactInformation.class);
    }

    public void authors(@DelegatesTo(value = People.class, strategy = Closure.DELEGATE_FIRST) Action<People> action) {
        backend().configureList("authors", action, People.class, false);
    }

    @DslValidate("authors")
    public People getAuthors() {
        return backend().getOrCreateList("authors", People.class);
    }

    public void contributors(@DelegatesTo(value = People.class, strategy = Closure.DELEGATE_FIRST) Action<People> action) {
        backend().configureList("contributors", action, People.class, false);
    }

    @DslValidate("contributors")
    public People getContributors() {
        return backend().getOrCreateList("contributors", People.class);
    }

    public void setIcon(@Nullable String path) {
        backend().putAt("icon", path);
    }

    public @Nullable String getIcon() {
        return (String) get("icon");
    }

    public void icons(@DelegatesTo(value = Icons.class, strategy = Closure.DELEGATE_FIRST) Action<Icons> action) {
        backend().configure("icon", action, Icons.class, false);
    }

    @DslValidate("icon")
    public Icons getIcons() {
        return backend().getOrCreate("icon", Icons.class);
    }

    public void jars(@DelegatesTo(value = NestedJarEntries.class, strategy = Closure.DELEGATE_FIRST) Action<NestedJarEntries> action) {
        backend().configureList("jars", action, NestedJarEntries.class, false);
    }

    @DslValidate("jars")
    public NestedJarEntries getJars() {
        return backend().getOrCreateList("jars", NestedJarEntries.class);
    }

    public void entrypoints(@DelegatesTo(value = Entrypoints.class, strategy = Closure.DELEGATE_FIRST) Action<Entrypoints> action) {
        backend().configure("entrypoints", action, Entrypoints.class, false);
    }

    @DslValidate("entrypoints")
    public Entrypoints getEntrypoints() {
        return backend().getOrCreate("entrypoints", Entrypoints.class);
    }

    public void languageAdapters(@DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_FIRST) Action<GenericChronicleMap> action) {
        backend().configure("languageAdapters", action, GenericChronicleMap.class, false);
    }

    @DslValidate("languageAdapters")
    public GenericChronicleMap getLanguageAdapters() {
        return backend().getOrCreate("languageAdapters", GenericChronicleMap.class);
    }

    public void mixins(@DelegatesTo(value = Mixins.class, strategy = Closure.DELEGATE_FIRST) Action<Mixins> action) {
        backend().configureList("mixins", action, Mixins.class, false);
    }

    @DslValidate("mixins")
    public Mixins getMixins() {
        return backend().getOrCreateList("mixins", Mixins.class);
    }

    public @Nullable String getAccessWidener() {
        return (String) get("accessWidener");
    }

    public void setAccessWidener(@Nullable String accessWidener) {
        backend().putAt("accessWidener", accessWidener);
    }

    public void depends(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_FIRST) Action<Dependencies> action) {
        backend().configure("depends", action, Dependencies.class, false);
    }

    @DslValidate("depends")
    public Dependencies getDepends() {
        return backend().getOrCreate("depends", Dependencies.class);
    }

    public void recommends(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_FIRST) Action<Dependencies> action) {
        backend().configure("recommends", action, Dependencies.class, false);
    }

    @DslValidate("recommends")
    public Dependencies getRecommends() {
        return backend().getOrCreate("recommends", Dependencies.class);
    }

    public void suggests(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_FIRST) Action<Dependencies> action) {
        backend().configure("suggests", action, Dependencies.class, false);
    }

    @DslValidate("suggests")
    public Dependencies getSuggests() {
        return backend().getOrCreate("suggests", Dependencies.class);
    }

    public void conflicts(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_FIRST) Action<Dependencies> action) {
        backend().configure("conflicts", action, Dependencies.class, false);
    }

    @DslValidate("conflicts")
    public Dependencies getConflicts() {
        return backend().getOrCreate("conflicts", Dependencies.class);
    }

    public void breaks(@DelegatesTo(value = Dependencies.class, strategy = Closure.DELEGATE_FIRST) Action<Dependencies> action) {
        backend().configure("breaks", action, Dependencies.class, false);
    }

    @DslValidate("breaks")
    public Dependencies getBreaks() {
        return backend().getOrCreate("breaks", Dependencies.class);
    }

    public void custom(@DelegatesTo(value = GenericChronicleMap.class, strategy = Closure.DELEGATE_FIRST) Action<GenericChronicleMap> action) {
        backend().configure("custom", action, GenericChronicleMap.class, false);
    }

    @DslValidate("custom")
    public GenericChronicleMap getCustom() {
        return backend().getOrCreate("custom", GenericChronicleMap.class);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("id") instanceof String string)) {
            throw new IllegalStateException("Expected 'id' to be present and a String");
        }
        if (!MOD_ID.asMatchPredicate().test(string)) {
            throw new IllegalStateException("Mod ID '" + string + "' is invalid; it must match the regex " + MOD_ID.pattern());
        }
        if (!(map.get("version") instanceof String)) {
            throw new IllegalStateException("Expected 'version' to be present and a String");
        }
    }

    static final Pattern MOD_ID = Pattern.compile("^[a-z][a-z0-9-_]{1,63}");
}
