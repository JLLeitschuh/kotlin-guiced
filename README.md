# Kotlin Guiced

[![Build Status](https://travis-ci.org/JLLeitschuh/kotlin-guiced.svg?branch=master)](https://travis-ci.org/JLLeitschuh/kotlin-guiced)
[ ![Download](https://api.bintray.com/packages/jlleitschuh/maven-artifacts/kotlin-guiced/images/download.svg) ](https://bintray.com/jlleitschuh/maven-artifacts/kotlin-guiced/_latestVersion)

A Kotlin API wrapper over the [Google Guice](https://github.com/google/guice) Dependency Injection library.

This library aims to encourage the use of Guice with Kotlin by simplifying the Guice API so it is more
fluent in the Kotlin programming language.

## NOTE:
Project is in very early stage of development. I plan to add helper functions as needed in a parallel cooperate internal
project and this project it may not comprehensively cover all of the methods out of the box.

## Examples:

### TypeLiteral
Because of java type erasure, Guice uses some strange java syntax to preserve type at runtime.
Many of these problems have been solved by Kotlin using inline functions with `reified` types.

In java you can declare a type literal with:
```java
new TypeLiteral<Map<Integer, String>>() {}
```
In Kotlin this syntax becomes even more verbose requiring more characters to write. 
```kotlin
object : TypeLiteral<Map<Integer, String>>() {}
```
This library provides helpers like the one below that is much cleaner to read.
```kotlin
typeLiteral<Map<Int, String>>()
```

### Guice Modules

TODO

## Project Structure
The intention is to structure this project such that Guice Core and each of it's respective extensions will
be in their own projects. The reasoning being that a library consumer can choose to depend upon only the Guice 
extensions they need an not get a transitive dependency on a Guice extention they don't need.


## Developers

### Requirements
Requires JDK 8 installed (Kotlin Compiler compiles to JDK 6 bytecode but requires JDK 8 to run).

### Building

This project uses Gradle to build/test/deploy code.
Run `./gradlew tasks` to se the various tasks this project supports.
