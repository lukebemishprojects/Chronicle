package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.ChronicleList;
import dev.lukebemish.chronicle.core.DslValidate;

public class MixinRequiredMods extends ChronicleList {
    public MixinRequiredMods(BackendList backend) {
        super(backend);
    }

    @DslValidate
    public static void validate(BackendList backendList) {
        for (Object item : backendList) {
            if (!(item instanceof String string)) {
                throw new IllegalStateException("Expected all items in mixin 'requiredMods' to be Strings");
            } else if (!Mod.MOD_ID.asMatchPredicate().test(string)) {
                throw new IllegalStateException("Mixin required mod ID '" + string + "' is invalid; it must match the regex " + Mod.MOD_ID.pattern());
            }
        }
    }
}
