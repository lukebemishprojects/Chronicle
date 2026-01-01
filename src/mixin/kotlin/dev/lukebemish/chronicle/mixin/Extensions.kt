package dev.lukebemish.chronicle.mixin

import kotlin.reflect.KClass

operator fun RequiredFeatures.set(idx: Int, feature: MixinFeature) = putAt(idx, feature)
operator fun RequiredFeatures.get(index: Int, type: KClass<MixinFeature>): MixinFeature = MixinFeature.valueOf(get(index) as String)
