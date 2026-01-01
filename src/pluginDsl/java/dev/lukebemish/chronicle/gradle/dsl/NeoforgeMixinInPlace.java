package dev.lukebemish.chronicle.gradle.dsl;

import dev.lukebemish.chronicle.core.MapMixin;
import dev.lukebemish.chronicle.core.RequiresDsl;
import dev.lukebemish.chronicle.neoforge.Mixin;

@RequiresDsl(ContextPlugin.class)
public interface NeoforgeMixinInPlace extends MapMixin<Mixin> {
}
