package org.jlleitschuh.guice

import com.google.inject.Module

/**
 * Creates a [Module] with the [BinderScope] being configured when [Module.configure]
 * is called by Guice.
 */
fun module(configure: BinderScope.() -> Unit) =
    Module { binder -> BinderScope(binder).configure() }