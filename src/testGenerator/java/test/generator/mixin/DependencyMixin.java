package test.generator.mixin;

import dev.lukebemish.chronicle.core.ListMixin;
import dev.lukebemish.chronicle.fabric.Dependency;

public interface DependencyMixin extends ListMixin<Dependency> {
    default void foo(String bar) {
        ListMixin.backend(this).add("bar: "+bar);
    }
}
