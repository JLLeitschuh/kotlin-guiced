package org.jlleitschuh.guice.binder

import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.LinkedBindingBuilder
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
class AnnotatedBindingBuilderScope<T : Any>(
    private val annotatedBindingBuilder: AnnotatedBindingBuilder<T>
) : LinkedBindingBuilderScope<T>(annotatedBindingBuilder), AnnotatedBindingBuilder<T> {

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    fun annotatedWith(annotationType: KClass<out Annotation>): LinkedBindingBuilder<T> =
        annotatedWith(annotationType.java)

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    override fun annotatedWith(annotationType: Class<out Annotation>): LinkedBindingBuilder<T> =
        LinkedBindingBuilderScope(annotatedBindingBuilder.annotatedWith(annotationType))

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    override fun annotatedWith(annotation: Annotation): LinkedBindingBuilder<T> =
        LinkedBindingBuilderScope(annotatedBindingBuilder.annotatedWith(annotation))

}