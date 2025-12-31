package dev.lukebemish.chronicle.core;

public abstract class ValueConfigurableChronicleMap<T, R> extends ConfigurableChronicleMap<T> {
    public ValueConfigurableChronicleMap(BackendMap backend) {
        super(backend);
    }

    abstract protected void configure(String key, R value);

    // Groovy compatibility

    @Override
    protected boolean methodMissingImpl(String name, Object args) {
        if(super.methodMissingImpl(name, args)) {
            return true;
        }
        if (args instanceof Object[] argsArray && argsArray.length == 1) {
            @SuppressWarnings("unchecked")
            R value = (R) argsArray[0];
            this.configure(name, value);
            return true;
        }
        return false;
    }
}
