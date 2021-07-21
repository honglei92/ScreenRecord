package com.example.a83661.screenrecorder.learn
import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
class Test {
    fun main(args: Array<String>) = runBlocking {
        repeat(100000) {
            this.launch {
                println("123")
            }
        }
    }
}