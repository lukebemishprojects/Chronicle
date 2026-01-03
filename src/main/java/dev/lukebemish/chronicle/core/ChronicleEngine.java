package dev.lukebemish.chronicle.core;

import groovy.lang.DelegatesTo;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class ChronicleEngine<T> {
    private final ChronicleContext context;
    private final View<T> view;
    private final Map<Class<?>, View<?>> views = new IdentityHashMap<>();
    private final Map<Class<?>, Class<?>> implementations = new IdentityHashMap<>();
    private final Set<Class<? extends ChronicleDsl>> dslPlugins = Collections.newSetFromMap(new IdentityHashMap<>());

    public ChronicleEngine(Class<T> clazz) {
        this(clazz, builder -> {});
    }

    public ChronicleEngine(Class<T> clazz, Consumer<ContextDataBuilder> contextData) {
        Deque<Class<? extends ChronicleDsl>> dsls = new ArrayDeque<>();
        if (clazz.isAnnotationPresent(RequiresDsl.class)) {
            dsls.addAll(Arrays.asList(clazz.getAnnotation(RequiresDsl.class).value()));
        }
        this.context = new ChronicleContext(this);
        contextData.accept(new ContextDataBuilder() {
            @Override
            public <R> void add(ContextKey<R> key, R value) {
                if (context.contextData.get(key) != null) {
                    throw new IllegalStateException("Context data for key " + key + " is already set");
                }
                context.setContextData(key, value);
            }
        });

        while (!dsls.isEmpty()) {
            var dsl = dsls.removeFirst();
            if (!dslPlugins.add(dsl)) {
                continue;
            }
            try {
                var ctor = dsl.getConstructor();
                var instance = ctor.newInstance();
                instance.register(new ChronicleDsl.Context() {
                    @Override
                    public <R> void registerImplementation(Class<R> type, Class<? extends R> implementation) {
                        ChronicleEngine.this.registerImplementation(type, implementation);
                    }

                    @Override
                    public void applyDsl(Class<? extends ChronicleDsl> dsl) {
                        dsls.add(dsl);
                    }

                    @Override
                    public void requiresContextData(ContextKey<?> key) {
                        var value = context.contextData.get(key);
                        if (value == null) {
                            throw new IllegalStateException("DSL " + dsl + " requires context data for key " + key + " but none was provided");
                        }
                    }
                });
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException("Could not initialize DSL " + dsl, e);
            }
        }
        this.view = context.view(clazz);
    }

    public interface ContextDataBuilder {
        <T> void add(ContextKey<T> key, T value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object execute(@DelegatesTo(type = "T") Action<T> action) {
        return switch (view) {
            case MapView<?> mapView -> executeMap(mapView, (Action) action);
            case ListView<?> listView -> executeList(listView, (Action) action);
        };
    }

    private <R extends ChronicleList> Object executeList(ListView<R> listView, Action<R> action) {
        var backend = new BackendList(context);
        var dsl = listView.wrap(backend);
        action.call(dsl);
        listView.validate(dsl);
        return backend.convert();
    }

    private <R extends ChronicleMap> Object executeMap(MapView<R> mapView, Action<R> action) {
        var backend = new BackendMap(context);
        var dsl = mapView.wrap(backend);
        action.call(dsl);
        mapView.validate(dsl);
        return backend.convert();
    }

    private void registerImplementation(Class<?> baseClazz, Class<?> implClazz) {
        if (implementations.containsKey(baseClazz)) {
            throw new IllegalStateException("Implementation for " + baseClazz + " is already registered: " + implementations.get(baseClazz));
        } else if (!baseClazz.isAssignableFrom(implClazz)) {
            throw new IllegalArgumentException("Implementation " + implClazz + " is not assignable to base class " + baseClazz);
        }
        implementations.put(baseClazz, implClazz);
    }

    @SuppressWarnings("unchecked")
    <R> View<R> view(Class<R> clazz) {
        if (ChronicleMap.class.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            var mapClazz = (Class<? extends ChronicleMap>) clazz;
            return (View<R>) mapView(mapClazz);
        } else if (ChronicleList.class.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            var listClazz = (Class<? extends ChronicleList>) clazz;
            return (View<R>) listView(listClazz);
        } else {
            throw new IllegalArgumentException("No view available for class: " + clazz);
        }
    }

    @SuppressWarnings("unchecked")
    <R extends ChronicleMap> MapView<R> mapView(Class<R> clazz) {
        return (MapView<R>) views.computeIfAbsent(
            findImpl(clazz),
            c -> locateView(c, BackendMap.class)
        );
    }

    @SuppressWarnings("unchecked")
    <R extends ChronicleList> ListView<R> listView(Class<R> clazz) {
        return (ListView<R>) views.computeIfAbsent(
            findImpl(clazz),
            c -> locateView(c, BackendList.class)
        );
    }

    private Class<?> findImpl(Class<?> baseClazz) {
        Class<?> located;
        while ((located = implementations.get(baseClazz)) != null) {
            baseClazz = located;
        }
        return baseClazz;
    }

    private record MapViewImpl<T extends ChronicleMap>(Wrapper<T> wrapper, Validator validator) implements MapView<T> {
        private MapViewImpl(Wrapper<T> wrapper, @Nullable Validator validator) {
            this.wrapper = wrapper;
            this.validator = validator == null ? it -> {
            } : validator;
        }

        @Override
        public T wrap(BackendMap map) {
            return wrapper.wrap(map);
        }

        @Override
        public void validate(T map) {
            validator.validate(map);
        }

        private interface Validator {
            void validate(ChronicleMap map);
        }

        private interface BackendValidator extends Validator {
            void validate(BackendMap map);

            @Override
            default void validate(ChronicleMap map) {
                validate(map.backend());
            }
        }

        private record MapPropertyValidator<T extends ChronicleMap>(PropertyGetter<T> getter, String property) implements Validator {
            interface PropertyGetter<T extends ChronicleMap> {
                T get(ChronicleMap map);
            }

            @Override
            public void validate(ChronicleMap map) {
                if (map.backend().get(property) != null) {
                    ChronicleMap.validate(getter.get(map));
                }
            }
        }

        private record ListPropertyValidator<T extends ChronicleList>(PropertyGetter<T> getter, String property) implements Validator {
            interface PropertyGetter<T extends ChronicleList> {
                T get(ChronicleMap map);
            }

            @Override
            public void validate(ChronicleMap map) {
                if (map.backend().get(property) != null) {
                    ChronicleList.validate(getter.get(map));
                }
            }
        }

        private interface Wrapper<T extends ChronicleMap> {
            T wrap(BackendMap map);
        }
    }

    private record ListViewImpl<T extends ChronicleList>(Wrapper<T> wrapper,
                                                         Validator validator) implements ListView<T> {
        private ListViewImpl(Wrapper<T> wrapper, @Nullable Validator validator) {
            this.wrapper = wrapper;
            this.validator = validator == null ? it -> {
            } : validator;
        }

        @Override
        public T wrap(BackendList list) {
            return wrapper.wrap(list);
        }

        @Override
        public void validate(T list) {
            validator.validate(list);
        }

        private interface Validator {
            void validate(ChronicleList list);
        }

        private interface BackendValidator extends Validator {
            void validate(BackendList list);

            @Override
            default void validate(ChronicleList list) {
                validate(list.backend());
            }
        }

        private interface Wrapper<T extends ChronicleList> {
            T wrap(BackendList list);
        }
    }

    private <R> View<R> locateView(Class<R> clazz, Class<?> backendType) {
        if (clazz.isAnnotationPresent(RequiresDsl.class)) {
            var dslClass = clazz.getAnnotation(RequiresDsl.class).value();
            for (var dsl : dslClass) {
                if (!this.dslPlugins.contains(dsl)) {
                    throw new IllegalStateException("Class " + clazz + " requires DSLs " + dsl + " but context has " + this.dslPlugins);
                }
            }
        }
        try {
            var ctor = clazz.getConstructor(backendType);
            var ctorHandle = MethodHandles.lookup().unreflectConstructor(ctor);
            Class<?> baseType;
            Class<?> wrapperType;
            Class<?> validatorType;
            Class<?> backendValidatorType;
            if (backendType == BackendMap.class) {
                baseType = ChronicleMap.class;
                wrapperType = MapViewImpl.Wrapper.class;
                validatorType = MapViewImpl.Validator.class;
                backendValidatorType = MapViewImpl.BackendValidator.class;
            } else {
                baseType = ChronicleList.class;
                wrapperType = ListViewImpl.Wrapper.class;
                validatorType = ListViewImpl.Validator.class;
                backendValidatorType = ListViewImpl.BackendValidator.class;
            }
            var wrapper = LambdaMetafactory.metafactory(
                MethodHandles.lookup(),
                "wrap",
                MethodType.methodType(wrapperType),
                MethodType.methodType(baseType, backendType),
                ctorHandle,
                MethodType.methodType(clazz, backendType)
            ).dynamicInvoker().invoke();
            List<MethodHandle> validatorHandles = new ArrayList<>();
            var searched = new LinkedHashSet<Class<?>>();
            var queueToSearch = new ArrayDeque<Class<?>>();
            queueToSearch.add(clazz);
            while (!queueToSearch.isEmpty()) {
                var current = queueToSearch.removeFirst();
                if (!searched.add(current)) {
                    continue;
                }
                var parent = current.getSuperclass();
                if (parent != null && parent != Object.class) {
                    queueToSearch.add(parent);
                }
                queueToSearch.addAll(Arrays.asList(current.getInterfaces()));
            }
            List<Object> validators = new ArrayList<>();
            for (var declClazz : searched) {
                for (var method : declClazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(DslValidate.class)) {
                        if (method.getParameterCount() == 1 &&
                            method.getReturnType() == void.class &&
                            (method.getParameterTypes()[0].isAssignableFrom(clazz) || method.getParameterTypes()[0] == backendType)
                        ) {
                            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                                var handle = MethodHandles.lookup().unreflect(method);
                                validatorHandles.add(handle);
                            } else {
                                throw new IllegalStateException("@DslValidate method must be static: " + method);
                            }
                        } else if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                            var annotation = method.getAnnotation(DslValidate.class);
                            if (annotation.value().isEmpty()) {
                                throw new IllegalStateException("@DslValidate method must specify property name when used as instance method: " + method);
                            }
                            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                                throw new IllegalStateException("@DslValidate method without args must be an instance method: " + method);
                            }
                            if (backendType == BackendList.class) {
                                throw new IllegalStateException("@DslValidate instance method cannot be used in ListView: " + method);
                            }
                            var returnType = method.getReturnType();
                            var handle = MethodHandles.lookup().unreflect(method);
                            try {
                                if (ChronicleList.class.isAssignableFrom(returnType)) {
                                    MapViewImpl.ListPropertyValidator.PropertyGetter<?> propertyGetter = (MapViewImpl.ListPropertyValidator.PropertyGetter<?>) LambdaMetafactory.metafactory(
                                        MethodHandles.lookup(),
                                        "get",
                                        MethodType.methodType(MapViewImpl.ListPropertyValidator.PropertyGetter.class),
                                        MethodType.methodType(ChronicleList.class, ChronicleMap.class),
                                        handle,
                                        handle.type()
                                    ).getTarget().invoke();
                                    validators.add(new MapViewImpl.ListPropertyValidator<>(propertyGetter, annotation.value()));
                                } else if (ChronicleMap.class.isAssignableFrom(returnType)) {
                                    MapViewImpl.MapPropertyValidator.PropertyGetter<?> propertyGetter = (MapViewImpl.MapPropertyValidator.PropertyGetter<?>) LambdaMetafactory.metafactory(
                                        MethodHandles.lookup(),
                                        "get",
                                        MethodType.methodType(MapViewImpl.MapPropertyValidator.PropertyGetter.class),
                                        MethodType.methodType(ChronicleMap.class, ChronicleMap.class),
                                        handle,
                                        handle.type()
                                    ).getTarget().invoke();
                                    validators.add(new MapViewImpl.MapPropertyValidator<>(propertyGetter, annotation.value()));
                                } else {
                                    throw new IllegalStateException("@DslValidate method has invalid return type: " + method);
                                }
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            throw new IllegalStateException("@DslValidate method has invalid signature: " + method);
                        }
                    }
                }
            }
            Object validator = null;
            if (!validatorHandles.isEmpty()) {
                // Run all property validators _before_ root validators
                validators.addAll(validatorHandles.stream()
                    .map(validatorHandle -> {
                        try {
                            var paramType = validatorHandle.type().parameterType(0);
                            var usesBackendValidator = paramType == backendType;
                            return LambdaMetafactory.metafactory(
                                MethodHandles.lookup(),
                                "validate",
                                MethodType.methodType(usesBackendValidator ? backendValidatorType : validatorType),
                                MethodType.methodType(void.class, usesBackendValidator ? backendType : baseType),
                                validatorHandle,
                                validatorHandle.type()
                            ).dynamicInvoker().invoke();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList()
                );
                if (backendType == BackendMap.class) {
                    validator = (MapViewImpl.Validator) map -> {
                        for (var v : validators) {
                            ((MapViewImpl.Validator) v).validate(map);
                        }
                    };
                } else {
                    validator = (ListViewImpl.Validator) list -> {
                        for (var v : validators) {
                            ((ListViewImpl.Validator) v).validate(list);
                        }
                    };
                }
            }
            if (backendType == BackendMap.class) {
                @SuppressWarnings({"rawtypes", "unchecked"})
                var view = (View<R>) new MapViewImpl((MapViewImpl.Wrapper) wrapper, (MapViewImpl.Validator) validator);
                return view;
            } else {
                @SuppressWarnings({"rawtypes", "unchecked"})
                var view = (View<R>) new ListViewImpl((ListViewImpl.Wrapper) wrapper, (ListViewImpl.Validator) validator);
                return view;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
