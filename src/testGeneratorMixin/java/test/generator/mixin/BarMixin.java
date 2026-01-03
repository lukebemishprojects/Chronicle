package test.generator.mixin;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.MapMixin;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import test.generator.target.Bar;

public interface BarMixin extends MapMixin<Bar> {
    default void gizmo(@DelegatesTo(value = Gizmo.class, strategy = Closure.DELEGATE_ONLY) Action<Gizmo> action) {
        MapMixin.backend(this).configure("gizmo", action, Gizmo.class, true);
    }
}
