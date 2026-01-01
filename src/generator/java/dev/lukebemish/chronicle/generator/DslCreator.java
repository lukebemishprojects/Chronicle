package dev.lukebemish.chronicle.generator;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendList;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleDsl;
import dev.lukebemish.chronicle.core.ChronicleList;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslMixin;
import dev.lukebemish.chronicle.core.ListMixin;
import dev.lukebemish.chronicle.core.MapMixin;
import dev.lukebemish.chronicle.core.RequiresDsl;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DslCreator {
    private final Path output;
    private final List<Path> classpath;
    private final String packageName;
    private final List<String> entrypoints;

    private DslCreator(Path output, List<Path> classpath, String packageName, List<String> entrypoints) {
        this.output = output;
        this.classpath = classpath;
        this.packageName = packageName;
        this.entrypoints = entrypoints;
    }

    public static void main(String[] args) {
        var builder = new Builder();
        for (int i = 0; i < args.length; i++) {
            if (args.length < i + 2) {
                throw new IllegalArgumentException("Expected value after " + args[i]);
            }
            var key = args[i];
            i++;
            switch (key) {
                case "--output", "-o" -> {
                    builder.output(Path.of(args[i]));
                }
                case "--classpath", "-cp" -> {
                    builder.classpath(Arrays.stream(args[i].split(File.pathSeparator)).map(Path::of).toList());
                }
                case "--package" -> {
                    builder.packageName(args[i]);
                }
                case "--entrypoint" -> {
                    builder.addEntrypoint(args[i]);
                }
                default -> throw new IllegalArgumentException("Unknown argument: " + key);
            }
        }
        var creator = builder
            .build();
        creator.create();
    }

    public static class Builder {
        private @Nullable Path output;
        private final List<Path> classpath = new ArrayList<>();
        private @Nullable String packageName;
        private final List<String> entrypoints = new ArrayList<>();

        public Builder output(Path output) {
            this.output = output;
            return this;
        }

        public Builder classpath(Path path) {
            this.classpath.add(path);
            return this;
        }

        public Builder classpath(SequencedCollection<Path> paths) {
            this.classpath.addAll(paths);
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder addEntrypoint(String className) {
            this.entrypoints.add(className.replace('.', '/'));
            return this;
        }

        public DslCreator build() {
            Objects.requireNonNull(output, "Output path must be set");
            Objects.requireNonNull(packageName, "Package name must be set");
            if (classpath.isEmpty()) {
                throw new IllegalStateException("At least one classpath entry must be set");
            }
            if (entrypoints.isEmpty()) {
                throw new IllegalStateException("At least one entrypoint must be set");
            }
            return new DslCreator(output, classpath, packageName, entrypoints);
        }
    }

    public void create() {
        URL[] urls = classpath.stream()
            .map(path -> {
                try {
                    return path.toUri().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            })
            .toArray(URL[]::new);
        try (URLClassLoader loader = new URLClassLoader(urls, null)) {
            var allMixinClassNames = new LinkedHashSet<String>();
            for (var serviceFiles = loader.findResources("META-INF/services/"+ DslMixin.class.getName()); serviceFiles.hasMoreElements();) {
                var url = serviceFiles.nextElement();
                try (var stream = url.openStream()) {
                    var classNames = new String(stream.readAllBytes()).lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .map(line -> line.replace('.', '/'))
                        .toList();
                    allMixinClassNames.addAll(classNames);
                }
            }
            var originalEntrypoints = new LinkedHashSet<String>();
            originalEntrypoints.addAll(entrypoints);
            var implMap = new LinkedHashMap<String, String>();
            var transitiveDsls = new LinkedHashSet<String>();
            if (Files.exists(output)) {
                try (Stream<Path> walk = Files.walk(output)) {
                    walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
                }
            }
            Files.createDirectories(output);
            runDslCreator(loader, allMixinClassNames.stream().toList(), originalEntrypoints, implMap, transitiveDsls, output, packageName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private sealed interface ParsedType {
        boolean needsBridge();
        boolean varianceRespectingSuper(ParsedType other);
        boolean containsDslTypes();
        void dslReferences(Consumer<ClassHierarchy> consumer);
        String descriptor();

        ParsedType replace(SequencedMap<String, String> implMap);

        record Simple(String name) implements ParsedType {
            @Override
            public boolean needsBridge() {
                return false;
            }

            @Override
            public boolean varianceRespectingSuper(ParsedType other) {
                return this.equals(other);
            }

            @Override
            public boolean containsDslTypes() {
                return false;
            }

            @Override
            public void dslReferences(Consumer<ClassHierarchy> consumer) {}

            @Override
            public String descriptor() {
                return name;
            }

            @Override
            public ParsedType replace(SequencedMap<String, String> implMap) {
                return this;
            }

            @Override
            public String toString() {
                return name;
            }
        }
        record Parameterized(String name, List<ParsedType> typeArguments) implements ParsedType {
            @Override
            public boolean needsBridge() {
                return false;
            }

            @Override
            public boolean varianceRespectingSuper(ParsedType other) {
                return (other instanceof Parameterized(
                    String nameOther, List<ParsedType> typeArgumentsOther
                )) && nameOther.equals(this.name) && typeArgumentsOther.size() == this.typeArguments.size() &&
                    IntStream.range(0, typeArgumentsOther.size()).allMatch(idx -> this.typeArguments.get(idx).varianceRespectingSuper(typeArgumentsOther.get(idx)));
            }

            @Override
            public boolean containsDslTypes() {
                return typeArguments.stream().anyMatch(ParsedType::containsDslTypes);
            }

            @Override
            public void dslReferences(Consumer<ClassHierarchy> consumer) {
                for (var typeArg : typeArguments) {
                    typeArg.dslReferences(consumer);
                }
            }

            @Override
            public String descriptor() {
                return "L" + name + ";";
            }

            @Override
            public ParsedType replace(SequencedMap<String, String> implMap) {
                return new Parameterized(
                    name,
                    typeArguments.stream()
                        .map(arg -> arg.replace(implMap))
                        .toList()
                );
            }

            @Override
            public String toString() {
                return "L" + name + "<" +
                    typeArguments.stream().map(Object::toString).collect(Collectors.joining()) +
                    ">;";
            }
        }
        record Dsl(ClassHierarchy classHierarchy) implements ParsedType {
            @Override
            public boolean needsBridge() {
                return true;
            }

            @Override
            public boolean varianceRespectingSuper(ParsedType other) {
                return (other instanceof Dsl(ClassHierarchy classHierarchyOther)) &&
                    this.classHierarchy.isSubclassOf(classHierarchyOther);
            }

            @Override
            public boolean containsDslTypes() {
                return true;
            }

            @Override
            public void dslReferences(Consumer<ClassHierarchy> consumer) {
                consumer.accept(classHierarchy);
            }

            @Override
            public String descriptor() {
                return "L" + classHierarchy.name() + ";";
            }

            @Override
            public ParsedType replace(SequencedMap<String, String> implMap) {
                return new Simple(
                    "L" + implMap.getOrDefault(classHierarchy.name(), classHierarchy.name()) + ";"
                );
            }

            @Override
            public String toString() {
                return "L" + classHierarchy.name() + ";";
            }
        }

        static ParsedType from(String signature, HierarchyCache cache) {
            if (signature.startsWith("L")) {
                var chars = signature.toCharArray();
                int idx = 0;
                var builder = new StringBuilder();
                idx = eatSignature(chars, idx, builder);
                var mainType = builder.toString();
                if (mainType.contains("<")) {
                    var typeArguments = new ArrayList<ParsedType>();
                    var firstPart = mainType.substring(1, mainType.indexOf('<'));
                    var typeSignature = mainType.substring(mainType.indexOf('<') + 1, mainType.length() - 2); // exclude final '>;'
                    var idxInner = 0;
                    var typeSignatureChars = typeSignature.toCharArray();
                    while (idxInner < typeSignature.length()) {
                        builder.setLength(0);
                        idxInner = eatSignature(typeSignatureChars, idxInner, builder);
                        typeArguments.add(from(builder.toString(), cache));
                    }
                    return new Parameterized(
                        firstPart,
                        typeArguments
                    );
                } else {
                    String typeName = mainType.substring(1, mainType.length() - 1);
                    var classHierarchy = cache.tryFind(typeName);
                    if (classHierarchy != null && (classHierarchy.isSubclassOf(cache.chronicleList()) || classHierarchy.isSubclassOf(cache.chronicleMap()))) {
                        return new Dsl(classHierarchy);
                    } else {
                        return new Simple(mainType);
                    }
                }
            } else {
                return new Simple(signature);
            }
        }
    }

    private record MethodSignature(String returnType, List<String> parameterTypes) {
        static MethodSignature from(String signature) {
            var chars = signature.toCharArray();
            int idx = 0;
            var builder = new StringBuilder();
            if (chars[idx] == '<') {
                idx = eatTypeSignature(chars, idx, builder);
            }
            if (chars[idx] != '(') {
                throw new IllegalArgumentException("Expected '(' at index " + idx);
            }
            idx++;
            var parameterTypes = new ArrayList<String>();
            while (chars[idx] != ')') {
                builder.setLength(0);
                idx = eatSignature(chars, idx, builder);
                parameterTypes.add(builder.toString());
            }
            idx++; // eat ')'
            builder.setLength(0);
            eatSignature(chars, idx, builder);
            var returnType = builder.toString();
            return new MethodSignature(returnType, parameterTypes);
        }
    }

    private record ClassHierarchy(String name, List<ClassHierarchy> superTypes) {
        public boolean isSubclassOf(ClassHierarchy other) {
            if (name.equals(other.name)) {
                return true;
            }
            for (var superType : superTypes) {
                if (superType.isSubclassOf(other)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ClassHierarchy that)) return false;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {
            return "ClassHierarchy{" +
                "name='" + name + '\'' +
                '}';
        }

        public ClassNode findNode(HierarchyCache cache) {
            return parse(cache.loader, name);
        }
    }

    private record ImplClass(String name, List<ImplMember> members, boolean isList) {
        static @Nullable ImplClass from(HierarchyCache cache, String name) {
            var node = DslCreator.tryParse(cache.loader, name);
            if (node == null) {
                return null;
            }
            var hierarchy = cache.find(node);
            boolean isList = hierarchy.isSubclassOf(cache.chronicleList());
            boolean isMap = hierarchy.isSubclassOf(cache.chronicleMap());
            if (!isList && !isMap) {
                return null;
            }
            var members = getImplMembers(cache, node);
            return new ImplClass(name, members, isList);
        }

        SequencedSet<ClassHierarchy> dslReferences() {
            var dsls = new LinkedHashSet<ClassHierarchy>();
            for (var member : members) {
                dsls.addAll(member.dslReferences());
            }
            return dsls;
        }
    }

    private record ImplMember(String name, ParsedType returnType, List<ParsedType> parameterTypes) {
        public boolean varianceRespectingSuper(String name, ParsedType returnType, List<ParsedType> parameterTypes) {
            return this.name.equals(name) && this.returnType.varianceRespectingSuper(returnType) &&
                this.parameterTypes.size() == parameterTypes.size() &&
                IntStream.range(0, parameterTypes.size()).allMatch(idx -> this.parameterTypes.get(idx).varianceRespectingSuper(parameterTypes.get(idx)));
        }

        public SequencedSet<ClassHierarchy> dslReferences() {
            var dsls = new LinkedHashSet<ClassHierarchy>();
            returnType.dslReferences(dsls::add);
            for (var paramType : parameterTypes) {
                paramType.dslReferences(dsls::add);
            }
            return dsls;
        }
    }

    private static class HierarchyCache {
        private final URLClassLoader loader;

        private final Map<String, ClassHierarchy> cache = new HashMap<>();
        {
            cache.put(ChronicleList.class.getName().replace('.', '/'), new ClassHierarchy(
                ChronicleList.class.getName().replace('.', '/'),
                List.of()
            ));
            cache.put(ChronicleMap.class.getName().replace('.', '/'), new ClassHierarchy(
                ChronicleMap.class.getName().replace('.', '/'),
                List.of()
            ));
        }

        private ClassHierarchy chronicleList() {
            return cache.get(ChronicleList.class.getName().replace('.', '/'));
        }

        private ClassHierarchy chronicleMap() {
            return cache.get(ChronicleMap.class.getName().replace('.', '/'));
        }

        private HierarchyCache(URLClassLoader loader) {
            this.loader = loader;
        }

        public @Nullable ClassHierarchy tryFind(String name) {
            if (cache.containsKey(name)) {
                return cache.get(name);
            }
            var node = DslCreator.tryParse(loader, name);
            if (node == null) {
                return null;
            }
            return find(node);
        }

        public ClassHierarchy find(ClassNode node) {
            var existing = cache.get(node.name);
            if (existing == null) {
                var superNames = new ArrayList<String>();
                if (node.superName != null) {
                    superNames.add(node.superName);
                }
                superNames.addAll(node.interfaces);
                var superTypes = new ArrayList<ClassHierarchy>();
                for (var superName : superNames) {
                    var hierarchy = tryFind(superName);
                    if (hierarchy != null) {
                        superTypes.add(hierarchy);
                    }
                }
                existing = new ClassHierarchy(
                    node.name,
                    superTypes
                );
                cache.put(node.name, existing);
            }
            return existing;
        }
    }

    private static List<ImplMember> getImplMembers(HierarchyCache cache, ClassNode classNode) {
        // TODO: _technically_ speaking we'd want to do this in super classes resolving type parameters and such. For now, let's not handle that edge case
        var directImplMembers = new LinkedHashMap<String, Set<ImplMember>>();
        for (var method : classNode.methods) {
            if (!Modifier.isStatic(method.access) && Modifier.isPublic(method.access)) {
                var signature = method.signature != null ? method.signature : method.desc;
                var methodSig = MethodSignature.from(signature);
                var returnType = ParsedType.from(methodSig.returnType, cache);
                var parameterTypes = new ArrayList<ParsedType>();
                for (var paramTypeSig : methodSig.parameterTypes) {
                    parameterTypes.add(ParsedType.from(paramTypeSig, cache));
                }
                if (returnType.containsDslTypes() || parameterTypes.stream().anyMatch(ParsedType::containsDslTypes)) {
                    directImplMembers.computeIfAbsent(method.name, k -> new LinkedHashSet<>())
                        .add(new ImplMember(method.name, returnType, parameterTypes));
                }
            }
        }
        var fullImplMembers = new LinkedHashSet<ImplMember>();
        for (var members : directImplMembers.values()) {
            fullImplMembers.addAll(members);
        }
        for (var superType : cache.find(classNode).superTypes()) {
            var node = superType.findNode(cache);
            var superImplMembers = getImplMembers(cache, node);
            for (var superImplMember : superImplMembers) {
                var candidates = directImplMembers.get(superImplMember.name);
                if (candidates != null) {
                    var overridden = false;
                    for (var candidate : candidates) {
                        if (candidate.varianceRespectingSuper(
                            superImplMember.name,
                            superImplMember.returnType,
                            superImplMember.parameterTypes
                        )) {
                            overridden = true;
                            break;
                        }
                    }
                    if (!overridden) {
                        fullImplMembers.add(superImplMember);
                    }
                }
            }
        }
        return new ArrayList<>(fullImplMembers);
    }

    private static void runDslCreator(URLClassLoader loader, List<String> mixinClassNames, SequencedSet<String> targets, SequencedMap<String, String> implMap, SequencedSet<String> transitiveDsls, Path output, String packageName) {
        var cache = new HierarchyCache(loader);

        SequencedMap<String, List<String>> mixins = new LinkedHashMap<>();
        determineMixinTargets(loader, mixinClassNames, mixins);

        SequencedMap<String, SequencedSet<String>> referencesTo = new LinkedHashMap<>();
        SequencedMap<String, SequencedSet<String>> referencesFrom = new LinkedHashMap<>();
        SequencedSet<String> toExplore = new LinkedHashSet<>(targets);
        toExplore.addAll(mixinClassNames);
        var queued = new LinkedHashSet<>(toExplore);
        var explorationQueue = new ArrayDeque<>(toExplore);
        while (!explorationQueue.isEmpty()) {
            var name = explorationQueue.removeFirst();
            var node = tryParse(loader, name);
            if (node == null) {
                continue;
            }
            var references = new LinkedHashSet<String>();
            var implMethods = getImplMembers(cache, node);
            for (var method : implMethods) {
                method.dslReferences().forEach(dslType -> {
                    references.add(dslType.name());
                });
            }
            referencesTo.put(name, references);
            for (var referenced : references) {
                referencesFrom.computeIfAbsent(referenced, k -> new LinkedHashSet<>())
                    .add(name);
                if (!queued.contains(referenced)) {
                    queued.add(referenced);
                    explorationQueue.addLast(referenced);
                }
            }
        }
        var entrypoints = new LinkedHashSet<>(targets);

        // Get classes we will _actually_ end up implementing
        // These are:
        // - entrypoints
        // - mixin target classes (transitively) referenced by entrypoints
        // - classes (transitively) referenced by entrypoints that (transitively) reference mixin target classes
        // First, make transitive reference maps
        SequencedMap<String, SequencedSet<String>> referencesToTransitively = makeTransitive(referencesTo);
        SequencedMap<String, SequencedSet<String>> referencesFromTransitively = makeTransitive(referencesFrom);

        SequencedSet<String> referencedByEntrypoints = new LinkedHashSet<>();
        for (var entrypoint : entrypoints) {
            var referenced = referencesToTransitively.get(entrypoint);
            if (referenced != null) {
                referencedByEntrypoints.addAll(referenced);
            }
        }
        SequencedSet<String> referencesMixinTargets = new LinkedHashSet<>();
        for (var mixinTarget : mixins.sequencedKeySet()) {
            var referencers = referencesFromTransitively.get(mixinTarget);
            if (referencers != null) {
                referencesMixinTargets.addAll(referencers);
            }
        }
        referencesMixinTargets.removeIf(target -> !referencedByEntrypoints.contains(target));
        targets.addAll(referencesMixinTargets);

        for (var target : targets) {
            var implName = packageName.replace('.', '/') + "/" + target + "Impl";
            implMap.put(target, implName);
        }

        var pluginClassName = packageName.replace('.', '/') + "/DslPlugin";

        for (var target : targets) {
            var superNode = parse(loader, target);
            var implClass = Objects.requireNonNull(ImplClass.from(cache, target));
            var implName = implMap.get(target);
            var outputNode = new ClassNode();
            outputNode.version = Math.max(Opcodes.V21, superNode.version);
            outputNode.name = implName;
            outputNode.superName = superNode.name;
            outputNode.access = Opcodes.ACC_PUBLIC;
            outputNode.interfaces = new ArrayList<>();
            var matchingMixins = mixins.getOrDefault(target, List.of());
            outputNode.interfaces.addAll(matchingMixins);
            var allMethods = new ArrayList<>(implClass.members);
            for (var mixin : matchingMixins) {
                var mixinMethods = getImplMembers(cache, parse(loader, mixin));
                allMethods.addAll(mixinMethods);
            }
            allMethods.removeIf(member -> member.dslReferences().stream().noneMatch(it -> targets.contains(it.name())));
            var ctorMethod = new MethodNode(
                Opcodes.ACC_PUBLIC,
                "<init>",
                MethodType.methodType(void.class, implClass.isList ? BackendList.class : BackendMap.class).descriptorString(),
                null,
                null
            );
            ctorMethod.visitCode();
            ctorMethod.visitVarInsn(Opcodes.ALOAD, 0);
            ctorMethod.visitVarInsn(Opcodes.ALOAD, 1);
            ctorMethod.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                superNode.name,
                "<init>",
                MethodType.methodType(void.class, implClass.isList ? BackendList.class : BackendMap.class).descriptorString(),
                false
            );
            ctorMethod.visitInsn(Opcodes.RETURN);
            ctorMethod.visitEnd();
            outputNode.methods.add(ctorMethod);
            for (var member : allMethods) {
                var newSignature = new StringBuilder("(");
                var newDescriptor = new StringBuilder("(");
                var oldDescriptor = new StringBuilder("(");
                for (var parsedType : member.parameterTypes) {
                    oldDescriptor.append(parsedType.descriptor());
                    var replacedType = parsedType.replace(implMap);
                    newDescriptor.append(replacedType.descriptor());
                    newSignature.append(replacedType);
                }
                oldDescriptor.append(")").append(member.returnType.descriptor());
                var replacedReturnType = member.returnType.replace(implMap);
                newDescriptor.append(")").append(replacedReturnType.descriptor());
                newSignature.append(")").append(replacedReturnType);
                var methodNode = new MethodNode(
                    Opcodes.ACC_PUBLIC,
                    member.name,
                    newDescriptor.toString(),
                    newSignature.toString(),
                    null
                );
                for (int i = 0; i < member.parameterTypes.size(); i++) {
                    var parsedType = member.parameterTypes.get(i);
                    if (parsedType instanceof ParsedType.Parameterized(var name, var typeArguments) && name.equals(Type.getInternalName(Action.class)) && typeArguments.size() == 1 && typeArguments.getFirst().replace(implMap) instanceof ParsedType.Simple(var simpleDescriptor)) {
                        var annotationVisitor = methodNode.visitParameterAnnotation(
                            i, "Lgroovy/lang/DelegatesTo;", true
                        );
                        annotationVisitor.visit("value", Type.getType(simpleDescriptor));
                        annotationVisitor.visit("strategy", 1); // Closure.DELEGATE_FIRST
                        annotationVisitor.visitEnd();
                    }
                }
                methodNode.visitCode();
                methodNode.visitVarInsn(Opcodes.ALOAD, 0);
                var newMethodType = Type.getMethodType(newDescriptor.toString());
                for (int i = 0; i < member.parameterTypes.size(); i++) {
                    var paramType = newMethodType.getArgumentTypes()[i];
                    methodNode.visitVarInsn(paramType.getOpcode(Opcodes.ILOAD), i + 1);
                }
                methodNode.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    superNode.name,
                    member.name,
                    oldDescriptor.toString(),
                    false
                );
                var returnType = Type.getType(newDescriptor.toString()).getReturnType();
                if (returnType.equals(Type.VOID_TYPE)) {
                    methodNode.visitInsn(Opcodes.RETURN);
                } else {
                    methodNode.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
                }
                methodNode.visitMaxs(0, 0);
                methodNode.visitEnd();
                outputNode.methods.add(methodNode);

                // TODO: annotate DelegatesTo on Actions

                if (!oldDescriptor.toString().contentEquals(newDescriptor)) {
                    // Needs a bridge method
                    var bridgeNode = new MethodNode(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
                        member.name,
                        oldDescriptor.toString(),
                        null,
                        null
                    );
                    bridgeNode.visitCode();
                    bridgeNode.visitVarInsn(Opcodes.ALOAD, 0);
                    var oldMethodType = Type.getMethodType(oldDescriptor.toString());
                    for (int i = 0; i < member.parameterTypes.size(); i++) {
                        var paramType = oldMethodType.getArgumentTypes()[i];
                        var newParamType = newMethodType.getArgumentTypes()[i];
                        bridgeNode.visitVarInsn(paramType.getOpcode(Opcodes.ILOAD), i + 1);
                        if (!paramType.equals(newParamType)) {
                            // Need to cast
                            var newName = newParamType.getClassName();
                            bridgeNode.visitTypeInsn(Opcodes.CHECKCAST, newName);
                        }
                    }
                    bridgeNode.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        implName,
                        member.name,
                        newDescriptor.toString(),
                        false
                    );
                    returnType = Type.getType(oldDescriptor.toString()).getReturnType();
                    if (returnType.equals(Type.VOID_TYPE)) {
                        bridgeNode.visitInsn(Opcodes.RETURN);
                    } else {
                        bridgeNode.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
                    }
                    bridgeNode.visitMaxs(0, 0);
                    bridgeNode.visitEnd();
                    outputNode.methods.add(bridgeNode);
                }
            }

            var annotationNode = new AnnotationNode(Type.getType(RequiresDsl.class).getDescriptor());
            annotationNode.values = List.of("value", List.of(Type.getObjectType(pluginClassName)));
            outputNode.visibleAnnotations = List.of(
                annotationNode
            );
            outputNode.visitEnd();
            var writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES);
            outputNode.accept(writer);
            var classBytes = writer.toByteArray();
            var classPath = output.resolve(implName + ".class");
            try {
                Files.createDirectories(classPath.getParent());
                Files.write(classPath, classBytes);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        var requiredPlugins = new LinkedHashSet<String>();
        var requiredDslSearch = new LinkedHashSet<String>();
        for (var target : targets) {
            var referenced = referencesToTransitively.get(target);
            if (referenced != null) {
                requiredDslSearch.addAll(referenced);
            }
        }
        for (var dslName : requiredDslSearch) {
            var node = parse(loader, dslName);
            if (node.visibleAnnotations == null) {
                continue;
            }
            for (var annotation : node.visibleAnnotations) {
                if (annotation.desc.equals(RequiresDsl.class.descriptorString())) {
                    var values = annotation.values;
                    for (int i = 0; i < values.size(); i += 2) {
                        var key = values.get(i);
                        if (!"value".equals(key)) {
                            continue;
                        }
                        if (values.get(i + 1) instanceof List<?> list) {
                            for (var value : list) {
                                if (value instanceof Type type) {
                                    requiredPlugins.add(type.getInternalName());
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        var dslPluginNode = new ClassNode();
        dslPluginNode.version = Opcodes.V21;
        dslPluginNode.name = pluginClassName;
        dslPluginNode.superName = Type.getInternalName(Object.class);
        dslPluginNode.access = Opcodes.ACC_PUBLIC;
        dslPluginNode.interfaces = List.of(Type.getInternalName(ChronicleDsl.class));
        var registerNode = new MethodNode(
            Opcodes.ACC_PUBLIC,
            "register",
            MethodType.methodType(void.class, ChronicleDsl.Context.class).descriptorString(),
            null,
            null
        );
        registerNode.visitCode();
        implMap.forEach((original, impl) -> {
            registerNode.visitVarInsn(Opcodes.ALOAD, 1);
            registerNode.visitLdcInsn(Type.getObjectType(original));
            registerNode.visitLdcInsn(Type.getObjectType(impl));
            registerNode.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                Type.getInternalName(ChronicleDsl.Context.class),
                "registerImplementation",
                MethodType.methodType(void.class, Class.class, Class.class).descriptorString(),
                true
            );
        });
        for (var plugin : requiredPlugins) {
            registerNode.visitVarInsn(Opcodes.ALOAD, 1);
            registerNode.visitLdcInsn(Type.getObjectType(plugin));
            registerNode.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                Type.getInternalName(ChronicleDsl.Context.class),
                "applyDsl",
                MethodType.methodType(void.class, Class.class).descriptorString(),
                true
            );
        }
        registerNode.visitInsn(Opcodes.RETURN);
        registerNode.visitMaxs(0, 0);
        registerNode.visitEnd();
        var dslPluginInitNode = new MethodNode(
            Opcodes.ACC_PUBLIC,
            "<init>",
            MethodType.methodType(void.class).descriptorString(),
            null,
            null
        );
        dslPluginInitNode.visitCode();
        dslPluginInitNode.visitVarInsn(Opcodes.ALOAD, 0);
        dslPluginInitNode.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            Type.getInternalName(Object.class),
            "<init>",
            MethodType.methodType(void.class).descriptorString(),
            false
        );
        dslPluginInitNode.visitInsn(Opcodes.RETURN);
        dslPluginInitNode.visitMaxs(0, 0);
        dslPluginInitNode.visitEnd();
        dslPluginNode.methods.add(dslPluginInitNode);
        dslPluginNode.methods.add(registerNode);
        dslPluginNode.visitEnd();

        var pluginWriter = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES);
        dslPluginNode.accept(pluginWriter);
        var pluginClassBytes = pluginWriter.toByteArray();
        var pluginClassPath = output.resolve(pluginClassName + ".class");
        try {
            Files.createDirectories(pluginClassPath.getParent());
            Files.write(pluginClassPath, pluginClassBytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static SequencedMap<String, SequencedSet<String>> makeTransitive(SequencedMap<String, SequencedSet<String>> original) {
        SequencedMap<String, SequencedSet<String>> transitive = new LinkedHashMap<>();
        for (var key : original.keySet()) {
            var stack = new ArrayDeque<>(original.get(key));
            var allRefs = new LinkedHashSet<>(stack);
            allRefs.add(key);
            while (!stack.isEmpty()) {
                var current = stack.removeFirst();
                var directRefs = original.get(current);
                if (directRefs != null) {
                    for (var ref : directRefs) {
                        if (!allRefs.contains(ref)) {
                            allRefs.add(ref);
                            stack.addLast(ref);
                        }
                    }
                }
            }
            transitive.put(key, allRefs);
        }
        return transitive;
    }

    private static void determineMixinTargets(URLClassLoader loader, List<String> mixinClassNames, SequencedMap<String, List<String>> mixins) {
        for (var mixin : mixinClassNames) {
            var node = parse(loader, mixin);
            // TODO: search in super types. Once again, requires resolving type parameters...
            int superIndex = node.interfaces.indexOf(Type.getInternalName(ListMixin.class));
            boolean listMixin = true;
            if (superIndex == -1) {
                superIndex = node.interfaces.indexOf(Type.getInternalName(MapMixin.class));
                listMixin = false;
                if (superIndex == -1) {
                    throw new RuntimeException("Mixin " + mixin + " does not extend DslMixin");
                }
            }
            String target = null;
            if (node.signature != null) {
                int index = 0;
                char[] chars = node.signature.toCharArray();
                if (chars[index] == '<') {
                    if (chars[index + 1] != '>') {
                        throw new IllegalArgumentException("Mixin class cannot have type parameters: " + mixin);
                    }
                    index += 2;
                }
                index = eatSignature(chars, index, null); // eat superclass
                for (int i = 0; i < superIndex - 1; i++) {
                    if (index >= chars.length) {
                        break;
                    }
                    index = eatSignature(chars, index, null);
                }
                if (index < chars.length) {
                    var builder = new StringBuilder();
                    eatSignature(chars, index, builder);
                    var interfaceSignature = builder.toString();
                    if (listMixin) {
                        var prefix = "L"+Type.getInternalName(ListMixin.class)+"<";
                        var suffix = ">;";
                        if (interfaceSignature.startsWith(prefix) && interfaceSignature.endsWith(suffix)) {
                            target = interfaceSignature.substring(prefix.length(), interfaceSignature.length() - suffix.length());
                        }
                    } else {
                        var prefix = "L"+Type.getInternalName(MapMixin.class)+"<";
                        var suffix = ">;";
                        if (interfaceSignature.startsWith(prefix) && interfaceSignature.endsWith(suffix)) {
                            target = interfaceSignature.substring(prefix.length(), interfaceSignature.length() - suffix.length());
                        }
                    }
                }
            }
            if (target == null) {
                throw new RuntimeException("Could not determine target type for mixin: " + mixin);
            } else if (!target.startsWith("L") || target.contains("<")) {
                throw new RuntimeException("Unsupported target type signature for mixin; mixins must target plain types: " + mixin + " -> " + target);
            }
            Type targetType = Type.getType(target);
            mixins.computeIfAbsent(targetType.getInternalName(), k -> new java.util.ArrayList<>())
                .add(mixin);
        }
    }

    private static int eatSignature(char[] chars, int index, @Nullable StringBuilder builder) {
        var head = chars[index];
        switch (head) {
            case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 'V', '*', '+', '-' -> {
                if (builder != null) builder.append(head);
                index++;
            }
            case '[' -> {
                if (builder != null) builder.append(head);
                index++;
                eatSignature(chars, index, builder);
            }
            case 'T', 'L' -> {
                while (chars[index] != ';') {
                    if (chars[index] == '<') {
                        index = eatTypeSignature(chars, index, builder);
                    } else {
                        if (builder != null) builder.append(chars[index]);
                        index++;
                    }
                }
                if (builder != null) builder.append(chars[index]);
                index++;
            }
            default -> {
                throw new IllegalArgumentException("Unexpected character '" + head + "' at index " + index);
            }
        }
        return index;
    }

    private static int eatTypeSignature(char[] chars, int index, @Nullable StringBuilder builder) {
        if (chars[index] != '<') {
            throw new IllegalArgumentException("Expected '<' at index " + index);
        }
        if (builder != null) builder.append(chars[index]);
        index++;
        while (chars[index] != '>') {
            index = eatSignature(chars, index, builder);
        }
        if (builder != null) builder.append(chars[index]);
        return index + 1;
    }

    private static ClassNode parse(URLClassLoader loader, String name) {
        var classNode = tryParse(loader, name);
        if (classNode == null) {
            throw new RuntimeException("Could not find class: " + name);
        }
        return classNode;
    }

    private static @Nullable ClassNode tryParse(URLClassLoader loader, String name) {
        // TODO: we should be able to cache this...
        var path = name + ".class";
        var url = loader.findResource(path);
        if (url == null) {
            return null;
        }
        try (var stream = url.openStream()) {
            var bytes = stream.readAllBytes();
            var classNode = new ClassNode();
            var reader = new ClassReader(bytes);
            reader.accept(classNode, 0);
            return classNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
