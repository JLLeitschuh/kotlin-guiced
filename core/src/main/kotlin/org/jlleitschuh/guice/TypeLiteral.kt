package org.jlleitschuh.guice

import com.google.inject.TypeLiteral

inline fun <reified T> typeLiteral() = object : TypeLiteral<T>() {}
