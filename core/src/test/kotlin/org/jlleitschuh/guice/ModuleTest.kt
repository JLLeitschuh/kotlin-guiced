package org.jlleitschuh.guice

import com.google.inject.AbstractModule

class ModuleTest {
    interface Interface

    class Implementation : Interface

    val simpleModule = module {
        bind(key<Interface>()).to(key<Implementation>())
    }

    val complicated = object : AbstractModule() {
        override fun configure() {
            bind(key<Interface>()).to(key<Implementation>())
        }

    }
}