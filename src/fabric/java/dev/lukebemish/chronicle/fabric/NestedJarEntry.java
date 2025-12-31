package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;

import java.util.Objects;

public class NestedJarEntry extends ChronicleMap {
    public NestedJarEntry(BackendMap backend) {
        super(backend);
    }

    public String getFile() {
        return (String) Objects.requireNonNull(get("file"));
    }

    public void setFile(String file) {
        set("file", file);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("file") instanceof String)) {
            throw new IllegalStateException("Expected 'file' to be present and a String");
        }
    }
}
