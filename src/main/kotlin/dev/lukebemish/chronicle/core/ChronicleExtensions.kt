@file:JvmSynthetic

package dev.lukebemish.chronicle.core

class ValueKind<T> private constructor() {
    companion object {
        val STRING = ValueKind<String>()
        val NUMBER = ValueKind<Number>()
        val BOOLEAN = ValueKind<Boolean>()
        val MAP = ValueKind<GenericChronicleMap>()
        val LIST = ValueKind<GenericChronicleList>()
    }
}

inline operator fun <reified T> ChronicleMap.get(key: String, type: ValueKind<T>): T? = get(key) as T?
inline operator fun <reified T> ChronicleList.get(index: Int, type: ValueKind<T>): T = get(index) as T

context(outer: ConfigurableChronicleMap<T>)
operator fun <T: Any> String.invoke(action: Action<T>) {
    outer.configure(this, action)
}

context(outer: ValueConfigurableChronicleMap<T, R>)
operator fun <T: Any, R: Any> String.invoke(value: R) {
    outer.configure(this, value)
}
