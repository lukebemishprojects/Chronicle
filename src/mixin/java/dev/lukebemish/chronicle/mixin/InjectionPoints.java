package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;

public class InjectionPoints extends ChronicleList {
    public InjectionPoints(BackendList backend) {
        super(backend);
    }
}
