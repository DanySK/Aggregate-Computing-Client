package it.unibo.aggregatecomputingclient

import org.junit.Test

import org.junit.Assert.*
import org.protelis.lang.ProtelisLoader

/**
 * Example local unit test, which will execute on the development machine (host).
 */
class ExampleUnitTest {
    @Test
    fun protelisLoader() {
        val program = ProtelisLoader.parse("module hello\n\n1")
    }
}
