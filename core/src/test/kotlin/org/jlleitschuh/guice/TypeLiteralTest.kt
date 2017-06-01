package org.jlleitschuh.guice

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec


class TypeLiteralTest : StringSpec() {
    init {
        "should keep type data" {
            val keySetType = typeLiteral<Map<Int, String>>().getReturnType(Map::class.java.getMethod("keySet"))
            keySetType.toString() shouldBe "java.util.Set<java.lang.Integer>"
        }
    }
}