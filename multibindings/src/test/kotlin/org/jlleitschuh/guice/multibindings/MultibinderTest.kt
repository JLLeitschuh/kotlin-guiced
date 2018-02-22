package org.jlleitschuh.guice.multibindings

import com.google.inject.AbstractModule
import org.junit.jupiter.api.Test

class MultibinderTest {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestAnnotation

    interface TestInterface

    @Test
    fun `should be able to bind an interface`() {
        object : AbstractModule() {
            override fun configure() {
                KotlinMultibinder.newSetBinder<TestInterface>(binder())
            }
        }
    }

    @Test
    fun `should be able to bind an interface and an annotation`() {
        object : AbstractModule() {
            override fun configure() {
                KotlinMultibinder.newSetBinder<TestInterface>(binder(), TestAnnotation::class)
            }
        }
    }
}