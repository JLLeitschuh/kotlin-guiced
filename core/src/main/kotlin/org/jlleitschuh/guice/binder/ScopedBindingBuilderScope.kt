package org.jlleitschuh.guice.binder

import com.google.inject.binder.ScopedBindingBuilder
import kotlin.reflect.KClass

class ScopedBindingBuilderScope(
    private val scopedBuilder: ScopedBindingBuilder
) : ScopedBindingBuilder by scopedBuilder {

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    fun `in`(scopeAnnotation: KClass<out Annotation>) =
        scopedBuilder.`in`(scopeAnnotation.java)
}