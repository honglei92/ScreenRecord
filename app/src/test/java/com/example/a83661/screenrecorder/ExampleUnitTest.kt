package com.example.a83661.screenrecorder

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun memoryTest() = runBlocking {
        repeat(100000) {
            this.launch {
                println("123")
            }
        }
    }
}
