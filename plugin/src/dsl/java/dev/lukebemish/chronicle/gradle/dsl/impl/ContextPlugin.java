package dev.lukebemish.chronicle.gradle.dsl.impl;

import dev.lukebemish.chronicle.core.ChronicleDsl;
import dev.lukebemish.chronicle.core.ContextKey;

import java.util.List;
import java.util.Optional;

public class ContextPlugin implements ChronicleDsl {
    public static final ContextKey<Optional<ClassScanner>> CLASS_SCANNER = new ContextKey<>("dev.lukebemish.chronicle.gradle.mixin-scan-paths");

    @Override
    public void register(Context context) {
        context.requiresContextData(CLASS_SCANNER);
    }

    public interface ClassScanner {
        List<String> findInPackage(String packageName);
    }
}
