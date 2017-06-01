buildscript {
    repositories {
        maven {
            mavenCentral()
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1.1")
    }
}

version = "0.0.1-SNAPSHOT"

subprojects {
    apply {
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compile(kotlinModule("stdlib"))
        compile(kotlinModule("reflect"))
        testCompile(group = "io.kotlintest", name = "kotlintest", version = "2.0.2")
    }
}