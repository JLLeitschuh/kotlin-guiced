import org.gradle.api.publish.maven.MavenPublication

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1.1")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
    }
}

allprojects {
    apply {
        plugin("com.jfrog.bintray")
    }
    version = "0.0.1"
    group = "org.jlleitschuh.guice"
    bintray {
        user = properties["bintray.publish.user"].toString()
        key = properties["bintray.publish.key"].toString()
        with(pkg) {
            repo = "maven-artifacts"
            name = "kotlin-guiced"
            setLicenses("MIT")
            setLabels("guice", "kotlin", "dependency injection")
            vcsUrl = "https://github.com/JLLeitschuh/kotlin-guiced"
            githubRepo = "https://github.com/JLLeitschuh/kotlin-guiced"
        }
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compile(kotlinModule("stdlib"))
        compile(kotlinModule("reflect"))
        testCompile(group = "io.kotlintest", name = "kotlintest", version = "2.0.2")
    }

    afterEvaluate {
        // This ensures that the entire project's configuration has been resolved before creating a publish artifact.
        publishing {
            publications {
                create<MavenPublication>(name) {
                    from(components["java"])
                }
            }
        }
    }
}

/**
 * Retrieves or configures the [bintray][com.jfrog.bintray.gradle.BintrayExtension] project extension.
 */
fun Project.`bintray`(configure: com.jfrog.bintray.gradle.BintrayExtension.() -> Unit = {}) =
    extensions.getByName<com.jfrog.bintray.gradle.BintrayExtension>("bintray").apply { configure() }

/**
 * Retrieves or configures the [publishing][org.gradle.api.publish.PublishingExtension] project extension.
 */
fun Project.`publishing`(configure: org.gradle.api.publish.PublishingExtension.() -> Unit = {}) =
    extensions.getByName<org.gradle.api.publish.PublishingExtension>("publishing").apply { configure() }
