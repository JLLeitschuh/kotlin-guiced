package org.jlleitschuh.guice.binder

import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.binder.LinkedBindingBuilder
import org.jlleitschuh.guice.typeLiteral
import kotlin.reflect.KClass

open class LinkedBindingBuilderScope<T : Any>(
    private val linkedBindingBuilder: LinkedBindingBuilder<T>) :
    LinkedBindingBuilder<T> by linkedBindingBuilder {
    /*
     * I've thought about providing `to` as an infix operation, but realized it's a terrible idea.
     * The possible confusion of meaning between this `to` and the the one that creates a `Pair` is way too easy
     * a mistake to make.
     */

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    inline fun <reified T2 : T> to() =
        to(typeLiteral<T2>())

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    fun to(implementation: KClass<out T>): ScopedBindingBuilderScope =
        to(implementation.java)

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    override fun to(implementation: Class<out T>): ScopedBindingBuilderScope =
        ScopedBindingBuilderScope(linkedBindingBuilder.to(implementation))

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    override fun to(implementation: TypeLiteral<out T>): ScopedBindingBuilderScope =
        ScopedBindingBuilderScope(linkedBindingBuilder.to(implementation))

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    override fun to(targetKey: Key<out T>): ScopedBindingBuilderScope =
        ScopedBindingBuilderScope(linkedBindingBuilder.to(targetKey))

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    fun toProvider(providerType: KClass<out javax.inject.Provider<out T>>): ScopedBindingBuilderScope =
        ScopedBindingBuilderScope(linkedBindingBuilder.toProvider(providerType.java))

    /**
     * See the EDSL examples at [com.google.inject.Binder].
     */
    inline fun <reified P : javax.inject.Provider<out T>> toProvider() =
        toProvider(P::class)
}