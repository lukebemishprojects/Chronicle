package dev.lukebemish.chronicle.core

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@PackageScope
@CompileStatic
class GroovyUtilsNative {
    static <T> void executeClosure(Closure<?> closure, T value) {
        var rehydrated = closure.rehydrate(value, closure.getOwner(), closure.getThisObject())
        rehydrated.setResolveStrategy(Closure.DELEGATE_FIRST)
        rehydrated.call(value)
    }
}
