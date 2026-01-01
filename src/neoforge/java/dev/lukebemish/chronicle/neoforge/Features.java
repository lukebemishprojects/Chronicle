package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import org.jspecify.annotations.Nullable;

public class Features extends ChronicleMap {
    public Features(BackendMap backend) {
        super(backend);
    }

    public @Nullable String getJavaVersion() {
        return (String) backend().get("javaVersion");
    }

    public void setJavaVersion(@Nullable String javaVersion) {
        backend().putAt("javaVersion", javaVersion);
    }

    public @Nullable String getOpenGlVersion() {
        return (String) backend().get("openGLVersion");
    }

    public void setOpenGlVersion(@Nullable String openGlVersion) {
        backend().putAt("openGLVersion", openGlVersion);
    }
}
