import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.wrapper.Wrapper
import org.junit.platform.console.options.Details

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
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.1.0-M1")
    }
}
plugins {
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

val jacocoTestResultTaskName = "jacocoJunit5TestReport"

subprojects {
    apply {
        plugin("com.jfrog.bintray")
        plugin("kotlin")
        plugin("maven-publish")
        plugin("java-library")
        plugin("org.junit.platform.gradle.plugin")
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
        "testRuntime"(junitJupiter("junit-jupiter-engine"))
        "testRuntime"(create(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.1.0-M1"))
    }

    junitPlatform {
        details = Details.VERBOSE

        filters {
            includeClassNamePatterns(".*Test", ".*Tests", ".*Spec")
        }
    }

    // Below, configure jacoco code coverage on all Junit 5 tests.
    val junitPlatformTest: JavaExec by tasks

    jacoco {
        applyTo(junitPlatformTest)
    }

    val sourceSets = java.sourceSets

    task<JacocoReport>(jacocoTestResultTaskName) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Generates code coverage report for the ${junitPlatformTest.name} task."

        executionData(junitPlatformTest)
        dependsOn(junitPlatformTest)

        sourceSets(sourceSets["main"])
        sourceDirectories = files(sourceSets["main"].allSource.srcDirs)
        classDirectories = files(sourceSets["main"].output)
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

val jacocoRootReport = task<JacocoReport>("jacocoRootReport") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Generates code coverage report for all sub-projects."

    val jacocoReportTasks =
        subprojects.map { it.tasks[jacocoTestResultTaskName] as JacocoReport }
    dependsOn(jacocoReportTasks)

    val allExecutionData = jacocoReportTasks.map { it.executionData }
    executionData(*allExecutionData.toTypedArray())

    // Pre-initialize these to empty collections to prevent NPE on += call below.
    additionalSourceDirs = files()
    sourceDirectories = files()
    classDirectories = files()

    subprojects.forEach { testedProject ->
        val sourceSets = testedProject.java.sourceSets
        this@task.additionalSourceDirs = this@task.additionalSourceDirs?.plus(files(sourceSets["main"].allSource.srcDirs))
        this@task.sourceDirectories += files(sourceSets["main"].allSource.srcDirs)
        this@task.classDirectories += files(sourceSets["main"].output)
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
            toolVersion = "0.8.1"
        }
    }
}

configurations.create(PUBLISHED_CONFIGURATION_NAME)

tasks.withType<Wrapper>().configureEach {
    description = "Configure the version of gradle to download and use"
    gradleVersion = "4.10.2"
    distributionType = Wrapper.DistributionType.ALL
}

fun DependencyHandler.junitJupiter(name: String) =
    create(group = "org.junit.jupiter", name = name, version = "5.1.0-M1")

/**
 * Configures the [junitPlatform][org.junit.platform.gradle.plugin.JUnitPlatformExtension] project extension.
 */
fun Project.`junitPlatform`(configure: org.junit.platform.gradle.plugin.JUnitPlatformExtension.() -> Unit) =
    extensions.configure("junitPlatform", configure)

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
