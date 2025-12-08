@file:JvmSynthetic

package dev.lukebemish.chronicle.fabric

context(outer: Dependencies)
operator fun String.invoke(value: List<String>) {
    outer.mod(this, value)
}
