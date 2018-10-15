package org.jlleitschuh.guice

import com.google.inject.Guice
import com.google.inject.util.Providers
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InjectorTest {

    @Test
    fun `getInstance generic acts as a key`() {
        val simpleModule = module {
            bind<Interface>().to<Implementation>()
        }
        val theInterface = Guice.createInjector(simpleModule).getInstance<Interface>()
        assertTrue(theInterface is Implementation)
    }

    @Test
    fun `getInstance with a null binding`() {
        val simpleModule = module {
            bind<Interface>().toProvider(Providers.of(null))
        }
        val theInterface = Guice.createInjector(simpleModule).getInstance<Interface>()
        assertFalse(theInterface is Implementation)
    }
}
