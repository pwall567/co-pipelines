# co-pipelines

[![Build Status](https://github.com/pwall567/co-pipelines/actions/workflows/build.yml/badge.svg)](https://github.com/pwall567/co-pipelines/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v2.0.21&color=7f52ff&logo=kotlin&logoColor=7f52ff)](https://github.com/JetBrains/kotlin/releases/tag/v2.0.21)
[![Maven Central](https://img.shields.io/maven-central/v/io.kstuff/co-pipelines?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.kstuff%22%20AND%20a:%22co-pipelines%22)

Kotlin coroutine implementation of the [pipelines](https://github.com/pwall567/pipelines.git) library.

The classes in this library mirror those in the earlier library, but use Kotlin coroutine functionality.
That is, the `accept` and `emit` functions are all suspend functions.

## Dependency Specification

The latest version of the library is 4.0, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>io.kstuff</groupId>
      <artifactId>co-pipelines</artifactId>
      <version>4.0</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'io.kstuff:co-pipelines:4.0'
```
### Gradle (kts)
```kotlin
    implementation("io.kstuff:co-pipelines:4.0")
```

Peter Wall

2025-01-29
