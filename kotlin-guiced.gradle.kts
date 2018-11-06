import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.wrapper.Wrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin.version")}")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    }
}
plugins {
    `lifecycle-base`
    jacoco
}
apply {
    plugin("com.jfrog.bintray")
    plugin("maven-publish")
}
val PUBLISHED_CONFIGURATION_NAME = "published"

allprojects {
    version = "0.0.5"
    group = "org.jlleitschuh.guice"

    repositories {
        mavenCentral()
    }
}

val jacocoTestResultTaskName = "jacocoTestReport"

subprojects {
    apply {
        plugin("com.jfrog.bintray")
        plugin("kotlin")
        plugin("maven-publish")
        plugin("java-library")
        plugin("jacoco")
    }

    val publicationName = "publication-$name"
    bintray {
        user = properties["bintray.publish.user"].toString()
        key = properties["bintray.publish.key"].toString()
        setPublications(publicationName)
        with(pkg) {
            repo = "maven-artifacts"
            name = "kotlin-guiced"
            publish = true
            setLicenses("MIT")
            setLabels("guice", "kotlin", "dependency injection")
            vcsUrl = "https://github.com/JLLeitschuh/kotlin-guiced.git"
            githubRepo = "https://github.com/JLLeitschuh/kotlin-guiced"
        }
    }

    dependencies {
        "compile"(kotlin(module = "stdlib", version = property("kotlin.version") as String))
        "compile"(kotlin(module = "reflect", version = property("kotlin.version") as String))

        "testCompile"(junitJupiter("junit-jupiter-api"))
        "testCompile"(junitJupiter("junit-jupiter-params"))
        "testCompile"(group = "com.natpryce", name = "hamkrest", version = "1.5.0.0")
        "testRuntime"(junitJupiter("junit-jupiter-engine"))
        "testRuntime"(create(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.3.1"))
    }

    tasks.withType<KotlinCompile>().configureEach {
        // Kotlin incremental compilation is enabled by default
        kotlinOptions {
            jvmTarget = "1.8"

            freeCompilerArgs += "-Xprogressive"

            /*
             * Enables strict null checking when calling java methods using jsr305 annotations.
             * https://kotlinlang.org/docs/reference/java-interop.html#jsr-305-support
             */
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }

    tasks.withType<Test>().configureEach {
        extensions.configure(typeOf<JacocoTaskExtension>()) {
            /*
             * Fix for Jacoco breaking Build Cache support.
             * https://github.com/gradle/gradle/issues/5269
             */
            isAppend = false
        }

        useJUnitPlatform {
            filter {
                includeTestsMatching("*Test")
                includeTestsMatching("*Tests")
                includeTestsMatching("*Spec")
            }
        }

        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.STARTED)
            displayGranularity = 0
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
        }

        reports.junitXml.destination = file("${rootProject.buildDir}/test-results/${project.name}")
    }

    tasks.withType<JacocoReport>().configureEach {
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }

    val sourceJarTask = task<Jar>("sourceJar") {
        from(java.sourceSets["main"].allSource)
        classifier = "sources"
    }

    afterEvaluate {
        // This ensures that the entire project's configuration has been resolved before creating a publish artifact.
        publishing {
            publications {
                create<MavenPublication>(publicationName) {
                    from(components["java"])
                    artifact(sourceJarTask)
                }
            }
        }
    }
}

val jacocoRootReport = tasks.register("jacocoRootReport", JacocoReport::class.java) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Generates code coverage report for all sub-projects."

    val jacocoReportTasks =
        subprojects
            .asSequence()
            .filter {
                // Filter out source sets that don't have tests in them
                // Otherwise, Jacoco tries to generate coverage data for tests that don't exist
                !it.java.sourceSets["test"].allSource.isEmpty
            }
            .map { it.tasks[jacocoTestResultTaskName] as JacocoReport }
            .toList()
    dependsOn(jacocoReportTasks)

    val allExecutionData = jacocoReportTasks.map { it.executionData }
    executionData(*allExecutionData.toTypedArray())

    // Pre-initialize these to empty collections to prevent NPE on += call below.
    additionalSourceDirs = files()
    sourceDirectories = files()
    classDirectories = files()

    subprojects.forEach { testedProject ->
        val sourceSets = testedProject.java.sourceSets
        this@register.additionalSourceDirs =
            this@register.additionalSourceDirs?.plus(files(sourceSets["main"].allSource.srcDirs))
        this@register.sourceDirectories += files(sourceSets["main"].allSource.srcDirs)
        this@register.classDirectories += files(sourceSets["main"].output)
    }

    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

allprojects {
    // Configures the Jacoco tool version to be the same for all projects that have it applied.
    pluginManager.withPlugin("jacoco") {
        // If this project has the plugin applied, configure the tool version.
        jacoco {
            toolVersion = "0.8.2"
        }
    }
}

configurations.create(PUBLISHED_CONFIGURATION_NAME)

tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
    dependsOn(jacocoRootReport)
}

tasks.withType<Wrapper>().configureEach {
    description = "Configure the version of gradle to download and use"
    gradleVersion = "4.10.2"
    distributionType = Wrapper.DistributionType.ALL
}

fun DependencyHandler.junitJupiter(name: String) =
    create(group = "org.junit.jupiter", name = name, version = "5.3.1")

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

/**
 * Retrieves the [java][org.gradle.api.plugins.JavaPluginConvention] project convention.
 */
val Project.`java`: org.gradle.api.plugins.JavaPluginConvention
    get() = convention.getPluginByName("java")
