@file:JvmSynthetic

package dev.lukebemish.chronicle.core

import kotlin.reflect.KClass

operator fun ChronicleMap.get(key: String, type: KClass<Number>): Number? = get(key) as Number?
operator fun ChronicleMap.get(key: String, type: KClass<String>): String? = get(key) as String?
operator fun ChronicleMap.get(key: String, type: KClass<Boolean>): Boolean? = get(key) as Boolean?
operator fun ChronicleMap.get(key: String, type: KClass<GenericChronicleMap>): GenericChronicleMap? = get(key) as GenericChronicleMap?
operator fun ChronicleMap.get(key: String, type: KClass<GenericChronicleList>): GenericChronicleList? = get(key) as GenericChronicleList?
operator fun ChronicleList.get(index: Int, type: KClass<Number>): Number = get(index) as Number
operator fun ChronicleList.get(index: Int, type: KClass<String>): String = get(index) as String
operator fun ChronicleList.get(index: Int, type: KClass<Boolean>): Boolean = get(index) as Boolean
operator fun ChronicleList.get(index: Int, type: KClass<GenericChronicleMap>): GenericChronicleMap = get(index) as GenericChronicleMap
operator fun ChronicleList.get(index: Int, type: KClass<GenericChronicleList>): GenericChronicleList = get(index) as GenericChronicleList

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
