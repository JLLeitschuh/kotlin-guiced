package org.jlleitschuh.guice

import com.google.inject.Injector

inline fun <reified T> Injector.getInstance(): T = getInstance(key<T>())
