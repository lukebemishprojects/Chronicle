package dev.lukebemish.chronicle.core;

public abstract class ConfigurableChronicleMap<T> extends ChronicleMap {
    public ConfigurableChronicleMap(BackendMap backend) {
        super(backend);
    }

    protected abstract void configure(String key, Action<T> action);

    // Groovy compatibility

    @SuppressWarnings("unchecked")
    protected boolean methodMissingImpl(String name, Object args) {
        if (args instanceof Object[] argsArray && argsArray.length == 1) {
            Action<T> action;
            if (argsArray[0] instanceof Action<?> instance) {
                action = (Action<T>) instance;
            } else {
                action = Utils.isGroovyPresent() ? GroovyUtils.tryAdaptClosure(argsArray[0]) : null;
            }
            if (action != null) {
                this.configure(name, action);
                return true;
            }
        }
        return false;
    }

    public void methodMissing(String name, Object args) {
        if (methodMissingImpl(name, args)) {
            return;
        }
        throw new UnsupportedOperationException("Method " + name + " with arguments " + args + " is not supported.");
    }
}
