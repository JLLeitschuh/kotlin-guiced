package org.jlleitschuh.guice

import com.google.inject.Key

inline fun <reified T> key() = object : Key<T>() {}
