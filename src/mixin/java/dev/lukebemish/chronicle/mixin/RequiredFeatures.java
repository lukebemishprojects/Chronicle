package dev.lukebemish.chronicle.mixin;

import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;

public class RequiredFeatures extends ChronicleList {
    public RequiredFeatures(BackendList backend) {
        super(backend);
    }

    public void putAt(int index, MixinFeature feature) {
        backend().putAt(index, feature.name());
    }

    public void add(MixinFeature feature) {
        backend().add(feature.name());
    }
}
