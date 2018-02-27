package org.jlleitschuh.guice

import com.google.inject.Key
import com.google.inject.PrivateBinder
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedElementBuilder
import kotlin.reflect.KClass

class PrivateBinderScope
internal constructor(private val privateBinder: PrivateBinder): BinderScope(privateBinder), PrivateBinder {

    override fun skipSources(vararg classesToSkip: Class<*>): PrivateBinderScope {
        return PrivateBinderScope(privateBinder.skipSources(*classesToSkip))
    }

    fun skipSources(vararg classesToSkip: KClass<*>): PrivateBinderScope=
        skipSources(*classesToSkip.map { it.java }.toTypedArray())

    override fun withSource(source: Any): PrivateBinder {
        return PrivateBinderScope(privateBinder.withSource(source))
    }

    override fun expose(key: Key<*>) {
        privateBinder.expose(key)
    }

    override fun expose(type: Class<*>): AnnotatedElementBuilder {
        return privateBinder.expose(type)
    }

    fun expose(type: KClass<*>): AnnotatedElementBuilder =
        expose(type.java)

    override fun expose(type: TypeLiteral<*>): AnnotatedElementBuilder {
        return privateBinder.expose(type)
    }
}