# co-pipelines

[![Build Status](https://github.com/pwall567/co-pipelines/actions/workflows/build.yml/badge.svg)](https://github.com/pwall567/co-pipelines/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v2.0.21&color=7f52ff&logo=kotlin&logoColor=7f52ff)](https://github.com/JetBrains/kotlin/releases/tag/v2.0.21)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.util/co-pipelines?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.util%22%20AND%20a:%22co-pipelines%22)

Kotlin coroutine implementation of the [pipelines](https://github.com/pwall567/pipelines.git) library.

The classes in this library mirror those in the earlier library, but use Kotlin coroutine functionality.
That is, the `accept` and `emit` functions are all suspend functions.

## Dependency Specification

The latest version of the library is 3.1, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.util</groupId>
      <artifactId>co-pipelines</artifactId>
      <version>3.1</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.util:co-pipelines:3.1'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.util:co-pipelines:3.1")
```

Peter Wall

2025-01-26
