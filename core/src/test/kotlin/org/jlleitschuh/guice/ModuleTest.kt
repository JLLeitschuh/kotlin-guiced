package org.jlleitschuh.guice

import com.google.inject.AbstractModule
import com.google.inject.CreationException
import com.google.inject.Guice
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `module binding exceptions provide meaningful exceptions`() {
        val module = module {
            bind<Interface>().toProvider<InterfaceProvider>()
            bind<Interface>().toProvider<InterfaceProvider2>()
        }
        val exception = assertThrows<CreationException> {
            Guice.createInjector(module)
        }

        val expectedString1 =
            "1) A binding to org.jlleitschuh.guice.Interface was already configured at org.jlleitschuh.guice.ModuleTest${'$'}module binding exceptions provide meaningful exceptions${'$'}module${'$'}1.invoke(ModuleTest.kt:"
        val expectedString2 =
            "at org.jlleitschuh.guice.ModuleTest${'$'}module binding exceptions provide meaningful exceptions${'$'}module${'$'}1.invoke(ModuleTest.kt:"

        assertThat(exception.message!!, containsSubstring(expectedString1) and containsSubstring(expectedString2))
    }
}