package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;

public class Icons extends ChronicleMap {
    public Icons(BackendMap backend) {
        super(backend);
    }

    public void set(int size, String path) {
        backend().set(String.valueOf(size), path);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        for (var entry : map) {
            try {
                Integer.parseInt(entry.key());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Expected icon size keys to be integers, but found: " + entry.key());
            }
        }
    }
}
