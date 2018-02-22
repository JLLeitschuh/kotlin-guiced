package org.jlleitschuh.guice

import com.google.inject.Binder
import kotlin.reflect.KClass

class BinderScope
internal constructor(private val binder: Binder) : Binder by binder {

    /**
     * See the EDSL examples at [Binder].
     */
    fun <T : Any> bind(klass: KClass<T>) =
        binder.bind(klass::class.java)
}