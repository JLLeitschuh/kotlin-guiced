package org.jlleitschuh.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModuleTest {

    @Test
    fun `simple module`() {
        val simpleModule = module {
            bind(key<Interface>()).to(key<Implementation>())
        }
        val injector = Guice.createInjector(simpleModule)
        val theInterface = injector.getInstance(key<Interface>())
        assertTrue(theInterface is Implementation)
    }

    @Test
    fun `simple module reified binding methods`() {
        val simpleModule = module {
            bind<Interface>().to<Implementation>()
        }
        val injector = Guice.createInjector(simpleModule)
        val theInterface = injector.getInstance(key<Interface>())
        assertTrue(theInterface is Implementation)
    }

    @Test
    fun `module not using key`() {
        val simpleModule = module {
            bind(Interface::class).to(Implementation::class)
        }
        val injector = Guice.createInjector(simpleModule)
        val theInterface = injector.getInstance(key<Interface>())
        assertTrue(theInterface is Implementation)
    }

    @Test
    fun `module using instance`() {
        val instance = Implementation()
        val simpleModule = module {
            bind(Interface::class).toInstance(instance)
        }
        val injector = Guice.createInjector(simpleModule)
        val theInterface = injector.getInstance(key<Interface>())
        assertSame(instance, theInterface)
    }

    @Test
    fun `complicated module`() {
        val complicated = object : AbstractModule() {
            override fun configure() {
                bind(key<Interface>()).to(key<Implementation>())
            }
        }
        val injector = Guice.createInjector(complicated)
        injector.getInstance(key<Interface>())
    }

    @Test
    fun `module binding to provider using reified methods`() {
        val module = module {
            bind<Interface>().toProvider<InterfaceProvider>()
        }
        val injector = Guice.createInjector(module)

        val first = injector.getInstance(key<Interface>())
        val second = injector.getInstance(key<Interface>())
        assertNotSame(first, second)
    }
}