package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import org.jspecify.annotations.Nullable;

public class ContactInformation extends ChronicleMap {
    public ContactInformation(BackendMap backend) {
        super(backend);
    }

    public @Nullable String getEmail() {
        return (String) get("email");
    }

    public void setEmail(@Nullable String email) {
        backend().putAt("email", email);
    }

    public @Nullable String getIrc() {
        return (String) get("irc");
    }

    public void setIrc(@Nullable String irc) {
        backend().putAt("irc", irc);
    }

    public @Nullable String getHomepage() {
        return (String) get("homepage");
    }

    public void setHomepage(@Nullable String homepage) {
        backend().putAt("homepage", homepage);
    }

    public @Nullable String getSources() {
        return (String) get("sources");
    }

    public void setSources(@Nullable String sources) {
        backend().putAt("sources", sources);
    }

    public @Nullable String getIssues() {
        return (String) get("issues");
    }

    public void setIssues(@Nullable String issues) {
        backend().putAt("issues", issues);
    }
}
