package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;

public class MixinClasses extends ChronicleList {
    public MixinClasses(BackendList backend) {
        super(backend);
    }
}
