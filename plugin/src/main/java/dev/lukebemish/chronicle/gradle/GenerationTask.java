package dev.lukebemish.chronicle.gradle;

import dev.lukebemish.chronicle.core.Action;

import java.io.Serializable;

public record GenerationTask<T>(String relativePath, Action<T> action, Class<T> clazz, ResultSerializer serializer) {
}
