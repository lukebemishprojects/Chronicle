@file:JvmSynthetic

package dev.lukebemish.chronicle.core

class ValueKind<T> private constructor(private val cast: (Any?) -> T, private val create: ((ChronicleContext) -> T)? = null) {
    internal fun check(value: Any?, context: ChronicleContext): T {
        return if (value == null && create != null) {
            create(context)
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
        val CREATE_MAP = ValueKind<GenericChronicleMap>({ it as GenericChronicleMap }, { context ->
            context.mapView(GenericChronicleMap::class.java).wrap(BackendMap(context))
        })
        val CREATE_LIST = ValueKind<GenericChronicleList>({ it as GenericChronicleList }, { context ->
            context.listView(GenericChronicleList::class.java).wrap(BackendList(context))
        })
    }
}

operator fun <T: Any?> ChronicleMap.get(key: String, type: ValueKind<T>): T? = type.check(get(key), backend().context())
operator fun <T: Any?> ChronicleList.get(index: Int, type: ValueKind<T>): T & Any = type.check(get(index), backend().context())!!

context(outer: ConfigurableChronicleMap<T>)
operator fun <T: Any> String.invoke(action: Action<T>) {
    outer.configure(this, action)
}

context(outer: ValueConfigurableChronicleMap<T, R>)
operator fun <T: Any, R: Any> String.invoke(value: R) {
    outer.configure(this, value)
}
