package org.jlleitschuh.guice

import javax.inject.Provider

interface Interface

class Implementation : Interface

class InterfaceProvider : Provider<Interface> {
    override fun get() = Implementation()
}