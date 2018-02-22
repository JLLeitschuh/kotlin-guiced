package org.jlleitschuh.guice

import com.google.inject.Binder
import com.google.inject.Module

fun module(configure: BinderScope.() -> Unit) =
    Module { binder -> BinderScope(binder).configure() }