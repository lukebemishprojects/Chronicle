package dev.lukebemish.chronicle.gradle.dsl;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.DslValidate;
import dev.lukebemish.chronicle.core.GenericChronicleList;
import dev.lukebemish.chronicle.core.MapMixin;
import dev.lukebemish.chronicle.gradle.dsl.impl.ContextPlugin;
import dev.lukebemish.chronicle.mixin.MixinConfig;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public interface MixinClassesScanValidate extends MapMixin<MixinConfig> {
    @DslValidate
    static void validate(BackendMap map) {
        var consumedClasses = new HashSet<String>();
        var packageName = map.get("package") instanceof String string ? string : null;
        handleMixinClasses(map, packageName, consumedClasses, "client", false);
        handleMixinClasses(map, packageName, consumedClasses, "server", false);
        handleMixinClasses(map, packageName, consumedClasses, "mixins", true);
    }

    private static void handleMixinClasses(BackendMap map, @Nullable String packageName, HashSet<String> consumedClasses, String key, boolean excludeDuplicates) {
        var mixinsForScope = map.get(key);
        if (mixinsForScope instanceof GenericChronicleList list) {
            var outVals = new ArrayList<>();
            for (var item : list) {
                if (item instanceof String string) {
                    if (string.startsWith(MixinClassesScanUtil.PLACEHOLDER_PREFIX)) {
                        var rest = string.substring(MixinClassesScanUtil.PLACEHOLDER_PREFIX.length());
                        if (packageName != null) {
                            var scanner = map.context().getContextData(ContextPlugin.CLASS_SCANNER)
                                .orElseThrow(() -> new IllegalStateException("Cannot scan for mixins when class scanning is disabled"));
                            scanner.findInPackage(packageName + (rest.isEmpty() ? "" : "." + rest)).forEach(mixinClass -> {
                                var adaptedMixinClass = (rest.isEmpty() ? "" : rest + ".") + mixinClass;
                                var added = consumedClasses.add(adaptedMixinClass);
                                if (added || !excludeDuplicates) {
                                    outVals.add(adaptedMixinClass);
                                }
                            });
                        } else {
                            throw new IllegalStateException("Cannot scan for mixin classes without a 'package' defined in MixinConfig");
                        }
                    } else {
                        outVals.add(item);
                    }
                } else {
                    outVals.add(item);
                }
            }
            map.putAt(key, outVals);
        }
    }
}
