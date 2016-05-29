/*
 * Copyright (c) 2016 Andrew O'Malley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.andrewoma.testczar

import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

class Around1Unit : TestBase() {

    val aroundClass = AroundTestClass(id = "c1", before = { log("Before Around1.1 class") }, after = { log("After Around1.1 class") })
    val aroundClass2 = AroundTestClass(id = "c2", before = { log("Before Around1.2 class") }, after = { log("After Around1.2 class") })
    val aroundTest = AroundTest(before = { log("Before Around1 test") }, after = { log("After Around1 test") })

    override val rules = listOf(aroundAllFoo, aroundAllBar, aroundClass, aroundClass2, aroundTest, nameLogger)

    @Test fun foo1() {
        log("In Around1.foo1")
    }

    @Test fun foo2() {
        log("In Around1.foo2")
    }
}

class Around2Unit : TestBase() {

    val aroundClass = AroundTestClass(before = { log("Before Around2 class") }, after = { log("After Around2 class") })
    val aroundTest = AroundTest(before = { log("Before Around2 test") }, after = { log("After Around2 test") })

    override val rules = listOf(aroundAllFoo, aroundAllBar, aroundClass, aroundTest, nameLogger)

    @Test fun foo1() {
        log("In Around2.foo1")
    }

    @Test fun foo2() {
        log("In Around2.foo2")
    }
}

@RunWith(Suite::class)
@Suite.SuiteClasses(Around1Unit::class, Around2Unit::class)
class FooSuite {
    companion object {
        @Suppress("unused")
        @BeforeClass @JvmStatic
        fun beforeClass() {
        }

        @Suppress("unused")
        @AfterClass @JvmStatic
        fun afterClass() {
            println(messages.joinToString("\n"))
            val expected = """
                Before foo
                Before bar
                Before Around1.1 class
                Before Around1.2 class
                Before Around1 test
                In Around1.foo1
                After Around1 test
                Before Around1 test
                In Around1.foo2
                After Around1 test
                After Around1.2 class
                After Around1.1 class
                Before Around2 class
                Before Around2 test
                In Around2.foo1
                After Around2 test
                Before Around2 test
                In Around2.foo2
                After Around2 test
                After Around2 class"""
            assertThat(expected.trimIndent()).isEqualTo(messages.joinToString("\n"))
        }
    }
}

private val messages = arrayListOf<String>()

private fun log(message: String) {
    messages.add(message)
    println(message)
}

private val aroundAllFoo = AroundAll("foo", before = { log("Before foo") }, after = { log("After foo") })
private val aroundAllBar = AroundAll("bar", before = { log("Before bar") }, after = { log("After bar") })

