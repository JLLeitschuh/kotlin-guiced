package org.jlleitschuh.guice.multibindings

import com.google.inject.Binder
import com.google.inject.multibindings.Multibinder
import org.jlleitschuh.guice.key
import org.jlleitschuh.guice.typeLiteral
import kotlin.reflect.KClass

object KotlinMultibinder {
    inline fun <reified T> newSetBinder(binder: Binder) = Multibinder.newSetBinder(binder, key<T>())!!

    inline fun <reified T> newSetBinder(binder: Binder, annotationType: KClass<out Annotation>) =
        Multibinder.newSetBinder(binder, typeLiteral<T>(), annotationType.java)!!
}
