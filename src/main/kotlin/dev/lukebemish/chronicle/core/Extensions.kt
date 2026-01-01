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

val STRING = ValueKind.STRING
val NUMBER = ValueKind.NUMBER
val BOOLEAN = ValueKind.BOOLEAN
val MAP = ValueKind.MAP
val LIST = ValueKind.LIST
val CREATE_MAP = ValueKind.CREATE_MAP
val CREATE_LIST = ValueKind.CREATE_LIST

operator fun <T> ChronicleMap.get(key: String, type: ValueKind<T>): T? = type.check(get(key), backend().context())
operator fun <T> ChronicleList.get(index: Int, type: ValueKind<T>): T & Any = type.check(get(index), backend().context())!!

context(outer: ConfigurableChronicleMap<T>)
operator fun <T: Any> String.invoke(action: Action<T>) {
    outer.configure(this, action)
}

context(outer: ValueConfigurableChronicleMap<T, R>)
operator fun <T: Any, R: Any> String.invoke(value: R) {
    outer.configure(this, value)
}

operator fun ChronicleMap.set(key: String, value: List<*>) = this.putAt(key, value)
operator fun ChronicleMap.set(key: String, value: Map<*, *>) = this.putAt(key, value)
operator fun ChronicleMap.set(key: String, value: String) = this.putAt(key, value)
operator fun ChronicleMap.set(key: String, value: Number) = this.putAt(key, value)
operator fun ChronicleMap.set(key: String, value: Boolean) = this.putAt(key, value)

operator fun ChronicleList.set(key: Int, value: List<*>) = this.putAt(key, value)
operator fun ChronicleList.set(key: Int, value: Map<*, *>) = this.putAt(key, value)
operator fun ChronicleList.set(key: Int, value: String) = this.putAt(key, value)
operator fun ChronicleList.set(key: Int, value: Number) = this.putAt(key, value)
operator fun ChronicleList.set(key: Int, value: Boolean) = this.putAt(key, value)

operator fun BackendMap.set(key: String, value: Any) = this.putAt(key, value)
operator fun BackendList.set(index: Int, value: Any) = this.putAt(index, value)
