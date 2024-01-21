package com.darkmoose117.coffee

import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertTrue("Check JVM is mentioned", Greeting().greet().contains("JVM"))
    }
}