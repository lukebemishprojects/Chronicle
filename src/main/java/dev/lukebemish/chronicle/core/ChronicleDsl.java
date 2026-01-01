package dev.lukebemish.chronicle.core;

public interface ChronicleDsl {
    void register(Context context);

    interface Context {
        <R> void registerImplementation(Class<R> type, Class<? extends R> implementation);
        void applyDsl(Class<? extends ChronicleDsl> dsl);
        void requiresContextData(ContextKey<?> key);
    }
}
