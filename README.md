# Chronicle

[![Version](https://img.shields.io/gradle-plugin-portal/v/dev.lukebemish.chronicle?style=for-the-badge&color=blue&label=Latest%20Version&prefix=v)](https://plugins.gradle.org/plugin/dev.lukebemish.chronicle)

Chronicle is a tool for building DSLs to generate structured data that can be executed on their own or embedded into a
Gradle DSL. These DSLs can be extended at arbitrary points to add more functionality while maintaining IDE support.

Chronicle's Gradle plugin bundles DSLs for generating mod metadata for Fabric and NeoForge.

## Getting Started

To get started with Chronicle in Gradle, apply the plugin in your `build.gradle`:

```gradle
plugins {
    id "dev.lukebemish.chronicle" version "<version>"
}
```

Then configure the generated mod metadata for your source set:

```gradle
chronicle.forSourceSet("main") {
    fabric {
        id = "yourmodid"
        // ...
    }
    // Or
    neoForge {
        mods.add {
            modId = "yourmodid"
            // ...
        }
    }
}
```
