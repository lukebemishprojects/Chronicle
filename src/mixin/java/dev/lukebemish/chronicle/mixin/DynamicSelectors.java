package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;

public class DynamicSelectors extends ChronicleList {
    public DynamicSelectors(BackendList backend) {
        super(backend);
    }
}
