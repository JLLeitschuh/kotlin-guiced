package org.jlleitschuh.guice

import com.google.inject.Module
import com.google.inject.PrivateModule

/**
 * Creates a [Module] with the [BinderScope] being configured when [Module.configure]
 * is called by Guice.
 */
fun module(configure: BinderScope.() -> Unit) =
    Module { binder -> BinderScope(binder).configure() }

/**
 * Creates a [PrivateModule] with the [PrivateBinderScope] being configured when [PrivateModule.configure]
 * is called by Guice.
 */
fun privateModule(configure: PrivateBinderScope.() -> Unit) =
    // The PrivateModule data type has some reflection checks to get the right type passed in. Easier to just use it.
    object : PrivateModule() {
        override fun configure() =
            configure.invoke(PrivateBinderScope(binder()))

    }