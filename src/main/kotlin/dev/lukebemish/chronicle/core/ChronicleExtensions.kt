@file:JvmSynthetic

package dev.lukebemish.chronicle.core

class ValueKind<T> private constructor(private val cast: (Any?) -> T, private val create: (() -> T)? = null) {
    internal fun check(value: Any?): T {
        return if (value == null && create != null) {
            create()
        } else {
            cast(value)
        }
    }

    companion object {
        val STRING = ValueKind<String?>({ it as String? })
        val NUMBER = ValueKind<Number?>({ it as Number? })
        val BOOLEAN = ValueKind<Boolean?>({ it as Boolean? })
        val MAP = ValueKind<GenericChronicleMap?>({ it as GenericChronicleMap? })
        val LIST = ValueKind<GenericChronicleList?>({ it as GenericChronicleList? })
        val CREATE_MAP = ValueKind<GenericChronicleMap>({ it as GenericChronicleMap }, {
            GenericChronicleMap(BackendMap())
        })
        val CREATE_LIST = ValueKind<GenericChronicleList>({ it as GenericChronicleList }, {
            GenericChronicleList(BackendList())
        })
    }
}

operator fun <T: Any?> ChronicleMap.get(key: String, type: ValueKind<T>): T? = type.check(get(key))
operator fun <T: Any?> ChronicleList.get(index: Int, type: ValueKind<T>): T & Any = type.check(get(index))!!

context(outer: ConfigurableChronicleMap<T>)
operator fun <T: Any> String.invoke(action: Action<T>) {
    outer.configure(this, action)
}

context(outer: ValueConfigurableChronicleMap<T, R>)
operator fun <T: Any, R: Any> String.invoke(value: R) {
    outer.configure(this, value)
}
