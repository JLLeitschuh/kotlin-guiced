package org.jlleitschuh.guice

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TypeLiteralTest {

    @Test
    fun `should keep type data`() {
        val keySetType = typeLiteral<Map<Int, String>>().getReturnType(Map::class.java.getMethod("keySet"))
        assertEquals("java.util.Set<java.lang.Integer>", keySetType.toString())
    }
}