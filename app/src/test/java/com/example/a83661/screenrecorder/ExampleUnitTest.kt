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

    @Test
    fun memoryThreadTest() {
        for (i in 0 until 100000) {
//            println("" + i)
            val runnable = Runnable {
                run { println(Thread.currentThread().name + i) }
            }
            Thread(runnable).start()
        }
    }

    @Test
    fun stackOverFlow() {
        getValue(1);
    }

    private fun getValue(i: Int) {
        while (i == 1) {
            getValue(i)
        }
    }
}
