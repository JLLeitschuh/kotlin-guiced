package org.jlleitschuh.guice.multibindings

import com.google.inject.AbstractModule
import org.jlleitschuh.guice.module
import org.junit.jupiter.api.Test

class MultibinderTest {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestAnnotation

    interface TestInterface

    @Test
    fun `should be able to bind an interface`() {
        module {
            KotlinMultibinder.newSetBinder<TestInterface>(binder())
        }
        object : AbstractModule() {
            override fun configure() {
                KotlinMultibinder.newSetBinder<TestInterface>(binder())
            }
        }
    }

    @Test
    fun `should be able to bind an interface and an annotation`() {
        module {
            KotlinMultibinder.newSetBinder<TestInterface>(binder(), TestAnnotation::class)
        }
        object : AbstractModule() {
            override fun configure() {
                KotlinMultibinder.newSetBinder<TestInterface>(binder(), TestAnnotation::class)
            }
        }
    }
}