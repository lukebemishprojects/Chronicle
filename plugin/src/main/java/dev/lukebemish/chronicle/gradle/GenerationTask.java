package dev.lukebemish.chronicle.gradle;

import dev.lukebemish.chronicle.core.Action;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

import java.io.Serializable;

public record GenerationTask<T>(String relativePath, Action<T> action, Class<T> clazz, ResultSerializer serializer) {
    @Input
    public String getRelativePath() {
        return relativePath;
    }

    @Nested
    public Action<T> getAction() {
        return action;
    }

    @Input
    public Class<T> getClazz() {
        return clazz;
    }

    @Nested
    public ResultSerializer getSerializer() {
        return serializer;
    }
}
