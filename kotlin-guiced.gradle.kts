import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.wrapper.Wrapper

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
apply {
    plugin("com.jfrog.bintray")
    plugin("maven-publish")
}
val PUBLISHED_CONFIGURATION_NAME = "published"

allprojects {
    version = "0.0.3"
    group = "org.jlleitschuh.guice"
}


subprojects {
    apply {
        plugin("com.jfrog.bintray")
        plugin("kotlin")
        plugin("maven-publish")
        plugin("java-library")
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

    repositories {
        mavenCentral()
    }

    dependencies {
        "compile"(kotlin("stdlib"))
        "compile"(kotlin("reflect"))
        "testCompile"(create(group = "io.kotlintest", name = "kotlintest", version = "2.0.2"))
    }

    val sourceJarTask = task<Jar>("sourceJar") {
        from(the<JavaPluginConvention>().sourceSets["main"].allSource)
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

configurations.create(PUBLISHED_CONFIGURATION_NAME)

task<Wrapper>("wrapper") {
    description = "Configure the version of gradle to download and use"
    gradleVersion = "4.1"
    distributionType = Wrapper.DistributionType.ALL
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
