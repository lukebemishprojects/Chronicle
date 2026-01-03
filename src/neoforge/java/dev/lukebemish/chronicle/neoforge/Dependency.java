package dev.lukebemish.chronicle.neoforge;

import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Dependency extends ChronicleMap {
    public Dependency(BackendMap backend) {
        super(backend);
    }

    public String getModId() {
        return (String) Objects.requireNonNull(backend().get("modId"));
    }

    public void setModId(String modId) {
        backend().putAt("modId", modId);
    }

    public @Nullable String getReason() {
        return (String) backend().get("reason");
    }

    public void setReason(@Nullable String reason) {
        backend().putAt("reason", reason);
    }

    public @Nullable DependencyType getType() {
        String type = (String) backend().get("type");
        if (type == null) {
            return null;
        }
        return dev.lukebemish.chronicle.neoforge.DependencyType.forValue(type);
    }

    public void setType(@Nullable DependencyType type) {
        backend().putAt("type", type instanceof DependencyType t ? t.getValue() : null);
    }

    public @Nullable String getVersionRange() {
        return (String) backend().get("versionRange");
    }

    public void setVersionRange(@Nullable String versionRange) {
        backend().putAt("versionRange", versionRange);
    }

    public @Nullable Ordering getOrdering() {
        String ordering = (String) backend().get("ordering");
        if (ordering == null) {
            return null;
        }
        return dev.lukebemish.chronicle.neoforge.Ordering.forValue(ordering);
    }

    public void setOrdering(@Nullable Ordering ordering) {
        backend().putAt("ordering", ordering instanceof Ordering o ? o.getValue() : null);
    }

    public @Nullable Side getSide() {
        String side = (String) backend().get("side");
        if (side == null) {
            return null;
        }
        return dev.lukebemish.chronicle.neoforge.Side.forValue(side);
    }

    public void setSide(@Nullable Side side) {
        backend().putAt("side", side instanceof Side s ? s.getValue() : null);
    }

    public @Nullable String getReferralUrl() {
        return (String) backend().get("referralUrl");
    }

    public void setReferralUrl(@Nullable String referralUrl) {
        backend().putAt("referralUrl", referralUrl);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("modId") instanceof String string)) {
            throw new IllegalArgumentException("Dependency 'modId' must be a string");
        }
        if (!Mod.MOD_ID.asMatchPredicate().test(string)) {
            throw new IllegalArgumentException("Dependency modId '" + string + "' is invalid; it must match the regex " + Mod.MOD_ID.pattern());
        }
    }
}
