package dev.lukebemish.chronicle.gradle.dsl;

import dev.lukebemish.chronicle.core.MapMixin;
import dev.lukebemish.chronicle.core.RequiresDsl;
import dev.lukebemish.chronicle.fabric.Mixin;

@RequiresDsl(ContextPlugin.class)
public interface FabricMixinInPlace extends MapMixin<Mixin> {
}
