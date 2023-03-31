package com.z100.geopal.util

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * Unit test for [Logger]
 *
 * @author Z-100
 * @since 2.0
 */
class LoggerTest {

    @Test
    fun testLogDebugNoParam() {

        val input = "No params"

        val logEntry = Logger.log(this.javaClass, input)

        assertEquals(input, logEntry)
    }

    @Test
    fun testLogDebugOneParam() {

        val input = "Param 1: {}"
        val inputParam = "Wow, it works"
        val parameterizedInput = input.replaceFirst("{}", inputParam)

        val logEntry = Logger.log(this.javaClass, input, inputParam)

        assertEquals(input, logEntry)
    }

    @Test
    fun testLogDebugTwoParams() {

        val input = "Param 1: {}. Param 2: {}"
        val inputParam = "Wow, it works"
        val inputParam2 = "Well this works too"

        var parameterizedInput = input.replaceFirst("{}", inputParam)
        parameterizedInput = input.replaceFirst("{}", inputParam2)

        val logEntry = Logger.log(this.javaClass, input, inputParam, inputParam2)

        assertEquals(input, logEntry)
    }
}
