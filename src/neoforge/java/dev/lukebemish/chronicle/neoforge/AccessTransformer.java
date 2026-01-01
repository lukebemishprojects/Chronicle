package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;

import java.util.Objects;

public class AccessTransformer extends ChronicleMap {
    public AccessTransformer(BackendMap backend) {
        super(backend);
    }

    public String getFile() {
        return (String) Objects.requireNonNull(backend().get("file"));
    }

    public void setFile(String file) {
        backend().putAt("file", file);
    }

    @DslValidate
    public static void validate(BackendMap backendMap) {
        if (!(backendMap.get("file") instanceof String)) {
            throw new IllegalStateException("Expected 'file' to be present and a String");
        }
    }
}
