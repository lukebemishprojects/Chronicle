package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class NeoforgeModsToml extends ChronicleMap {
    public NeoforgeModsToml(BackendMap backend) {
        super(backend);
    }

    public @Nullable String getModLoader() {
        return (String) backend().get("modLoader");
    }

    public void setModLoader(@Nullable String modLoader) {
        backend().putAt("modLoader", modLoader);
    }

    public @Nullable String getLoaderVersion() {
        return (String) backend().get("loaderVersion");
    }

    public void setLoaderVersion(@Nullable String loaderVersion) {
        backend().putAt("loaderVersion", loaderVersion);
    }

    public String getLicense() {
        return (String) Objects.requireNonNull(backend().get("license"));
    }

    public void setLicense(String license) {
        backend().putAt("license", license);
    }

    public @Nullable Boolean getShowAsResourcePack() {
        return (Boolean) backend().get("showAsResourcePack");
    }

    public void setShowAsResourcePack(@Nullable Boolean showAsResourcePack) {
        backend().putAt("showAsResourcePack", showAsResourcePack);
    }

    public @Nullable Boolean getShowAsDataPack() {
        return (Boolean) backend().get("showAsDataPack");
    }

    public void setShowAsDataPack(@Nullable Boolean showAsDataPack) {
        backend().putAt("showAsDataPack", showAsDataPack);
    }

    public void services(@DelegatesTo(value = Services.class, strategy = Closure.DELEGATE_FIRST) Action<Services> action) {
        backend().configureList("services", action, Services.class);
    }

    public void properties(@DelegatesTo(value = ReplacementProperties.class, strategy = Closure.DELEGATE_FIRST) Action<ReplacementProperties> action) {
        backend().configure("properties", action, ReplacementProperties.class);
    }

    public @Nullable String getIssueTrackerUrl() {
        return (String) backend().get("issueTrackerURL");
    }

    public void setIssueTrackerUrl(@Nullable String issueTrackerURL) {
        backend().putAt("issueTrackerURL", issueTrackerURL);
    }

    public void mods(@DelegatesTo(value = Mods.class, strategy = Closure.DELEGATE_FIRST) Action<Mods> action) {
        backend().configureList("mods", action, Mods.class);
    }

    // These seem to be allowed from the root neoforge.mods.toml file as well:
    // https://github.com/neoforged/FancyModLoader/blob/33ef217a31bc5238e4f8a7e4d337a3f6a1112794/loader/src/main/java/net/neoforged/fml/loading/moddiscovery/ModInfo.java#L86-L91
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

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("license") instanceof String)) {
            throw new IllegalStateException("Expected 'license' to be present and a String");
        }
        var logoFile = map.get("logoFile");
        if (logoFile != null) {
            if (!(logoFile instanceof String logoString)) {
                throw new IllegalStateException("Expected 'logoFile' to be a String if present");
            }
            if (!Mod.LOGO_FILE.asMatchPredicate().test(logoString)) {
                throw new IllegalStateException("Logo file name '" + logoString + "' is invalid; it must match the regex " + Mod.LOGO_FILE.pattern());
            }
        }
    }
}
