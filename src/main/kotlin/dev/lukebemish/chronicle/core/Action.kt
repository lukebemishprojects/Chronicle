package dev.lukebemish.chronicle.core

import org.gradle.api.HasImplicitReceiver
import org.jetbrains.annotations.ApiStatus

@HasImplicitReceiver
fun interface Action<in T: Any>: ActionLike<@UnsafeVariance T> {
    @ApiStatus.OverrideOnly
    operator fun T.invoke()
}
