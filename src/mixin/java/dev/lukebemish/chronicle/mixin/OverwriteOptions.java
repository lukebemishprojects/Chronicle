package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import org.jspecify.annotations.Nullable;

public class OverwriteOptions extends ChronicleMap {
    public OverwriteOptions(BackendMap backend) {
        super(backend);
    }

    public @Nullable Boolean getConformVisibility() {
        return (Boolean) backend().get("conformVisibility");
    }

    public void setConformVisibility(@Nullable Boolean conformVisibility) {
        backend().putAt("conformVisibility", conformVisibility);
    }

    public @Nullable Boolean getRequireAnnotations() {
        return (Boolean) backend().get("requireAnnotations");
    }

    public void setRequireAnnotations(@Nullable Boolean requireAnnotations) {
        backend().putAt("requireAnnotations", requireAnnotations);
    }
}
