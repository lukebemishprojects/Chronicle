package dev.lukebemish.chronicle.core

import org.gradle.api.HasImplicitReceiver
import org.jetbrains.annotations.ApiStatus

@HasImplicitReceiver
fun interface Action<T: Any> {
    @ApiStatus.OverrideOnly
    operator fun T.invoke()

    @ApiStatus.NonExtendable
    fun then(other: Action<T>): Action<T> {
        with(this) {
            return Action {
                this@with.call(this)
                other.call(this)
            }
        }
    }

    @ApiStatus.NonExtendable
    fun call(value: T) = Utils.invokeAction(value, this)
}
