package org.jlleitschuh.guice.multibindings

import com.google.inject.AbstractModule
import io.kotlintest.specs.StringSpec

class MultibinderTest : StringSpec() {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestAnnotation
    interface TestInterface
    init {
        "should be able to bind an interface" {
            object : AbstractModule() {
                override fun configure() {
                    KotlinMultibinder.newSetBinder<TestInterface>(binder())
                }
            }
        }
        "should be able to bind an interface and an annotation" {
            object : AbstractModule() {
                override fun configure() {
                    KotlinMultibinder.newSetBinder<TestInterface>(binder(), TestAnnotation::class)
                }
            }
        }
    }
}