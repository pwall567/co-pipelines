# co-pipelines

Kotlin coroutine implementation of the [pipelines](https://github.com/pwall567/pipelines.git) library.

The classes in this library mirror those in the earlier library, but use Kotlin coroutine functionality.
That is, the `accept` and `emit` functions are all suspend functions.

## Dependency Specification

The latest version of the library is 0.4, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.util</groupId>
      <artifactId>co-pipelines</artifactId>
      <version>0.4</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.util:co-pipelines:0.4'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.util:co-pipelines:0.4")
```

Peter Wall

2020-04-19
