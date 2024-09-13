# NoMoreInstance

**NoMoreInstance** is a highly efficient, lightweight Java library that offers robust object-pooling capabilities,
specifically designed to reduce the overhead of creating short-lived objects in mono-threaded or high-concurrency
environments. Designed for performance-critical applications.

- [✨ Features](#-features)
- [❓ When to Use](#-when-to-use)
- [⚙️ How to Use](#%EF%B8%8F-how-to-use)
  - [Import the dependency with Gradle or Maven](#import-the-dependency-with-gradle-or-maven)
  - [Create a Pool](#create-a-pool)
  - [What Concurrency Level to choose?](#what-concurrency-level-to-choose)
  - [What Pool type to choose?](#what-pool-type-to-choose)
- [📄 License](#-license)
- [🔌 Contributing](#-contributing)

## ✨ Features

- **Lightweight & Modular**: Easy to integrate with any Java project.
- **High-Concurrency Support**: Supports multiple levels of concurrency, including synchronized and lock-free
  operations.
- **MIT License**: Project under the MIT License, offering the freedom to use, modify, and distribute the software.

## ❓ When to Use

**NoMoreInstance** is perfect for applications that create a large number of ephemeral objects. By reusing objects, the
library helps reduce garbage collection pressure and improve performance. However, it's not suited for long-lived
objects, as object pooling is primarily beneficial for managing short-lived instances.

## ⚙️ How to Use

### Import the dependency with Gradle or Maven

**Latest version**: [![Release](https://jitpack.io/v/YvanMazy/NoMoreInstance.svg)](https://jitpack.io/#YvanMazy/NoMoreInstance)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.YvanMazy:NoMoreInstance:VERSION'
}
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.YvanMazy</groupId>
        <artifactId>NoMoreInstance</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

### Create a Pool

Example code:

````java
private final SweepCleanablePool<Vector> pool =
        Pool.<Vector>newBuilder().supplier(Vector::new) // Optional object creation supplier
                .cleaner(Vector::reset) // Optional cleaner for after use
                .concurrency(PoolConcurrency.LOCK_FREE) // Pool concurrency level
                .buildSweep(Vector.class, 10); // Build a SweepCleanablePool

public void myMethod() {
    final Vector vector = this.pool.get(); // Get free object instance

    this.pool.cleanAll(); // Clean all object instances
}
````

````java
private final CleanablePool<Vector> pool =
        Pool.<Vector>newBuilder().supplier(Vector::new) // Optional object creation supplier
                .cleaner(Vector::reset) // Optional cleaner for after use
                .concurrency(PoolConcurrency.SYNCHRONIZED) // Pool concurrency level
                .build(Vector.class, 10); // Build a CleanablePool

public void myMethod() {
    try (final Cleanable<Vector> cleanable = this.pool.get()) {
        final Vector vector = cleanable.value(); // Get free object instance
    } // Clean automatically by try-with-resource
}
````

### What Concurrency Level to choose?

The library offers three levels of concurrency control:

- **NOT_CONCURRENT**: No synchronization, suitable for single-threaded use cases.
- **SYNCHRONIZED**: Uses Java synchronization mechanisms for thread safety.
- **LOCK_FREE**: High-performance, non-blocking pool operations designed for highly concurrent environments.

### What Pool type to choose?

The library offers two pool types:

- **CleanablePool**: Offers fine control over object cleaning, where individual objects can be cleaned as needed.
  Particularly useful when the pool is used at very different times.
- **SweepCleanablePool**: Allows you to get an instance more easily, but there is no way to clean up a specific
  instance. It only allows you to clean up everything at once. This lack of flexibility allows for greatly increased
  performance. Faster but not suitable for all scenarios.

## 📄 License

This project is under the MIT License, offering the freedom to use, modify, and distribute the software. See
the [LICENSE](../LICENSE) file for more details.

## 🔌 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests to improve the library.