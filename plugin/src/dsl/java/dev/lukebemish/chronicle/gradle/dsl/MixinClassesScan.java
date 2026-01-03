package dev.lukebemish.chronicle.gradle.dsl;

import dev.lukebemish.chronicle.core.DslMixin;
import dev.lukebemish.chronicle.core.ListMixin;
import dev.lukebemish.chronicle.mixin.MixinClasses;

public interface MixinClassesScan extends ListMixin<MixinClasses> {
    default void scan() {
        DslMixin.instance(this).add(MixinClassesScanUtil.PLACEHOLDER_PREFIX);
    }

    default void scan(String subPackage) {
        DslMixin.instance(this).add(MixinClassesScanUtil.PLACEHOLDER_PREFIX + subPackage);
    }
}

final class MixinClassesScanUtil {
    static final String PLACEHOLDER_PREFIX = "__CHRONICLE_MIXIN_SCAN_PATH_.";
}
