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

import org.junit.AfterClass
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

/**
 * A base class for all tests that runs the defined rules in the order given prior to each test
 */
@RunWith(InheritableRuleRunner::class)
abstract class TestBase {
    companion object {
        val classListeners = hashMapOf<Class<*>, List<ClassCompletionListener>>()
        val runListeners = hashSetOf<RunCompletionListener>()

        @Suppress("unused")
        @AfterClass @JvmStatic
        fun afterClass() {
            val clazz = InheritableRuleRunner.lastFinished.get()
            if (clazz != null && clazz in classListeners) {
                // Execute the after class listeners in the reserve order of the before
                for (listener in classListeners[clazz]!!) {
                    listener.onClassComplete(clazz)
                }
            }
        }
    }

    open val rules: List<TestRule> = listOf()

    @InheritedRule fun rules() = Rules(rules.reversed())

    inner class Rules(val rules: List<TestRule>) : TestRule {
        init {
            val listeners = rules.filterIsInstance(ClassCompletionListener::class.java)
            if (listeners.isNotEmpty()) classListeners.put(this@TestBase.javaClass, listeners)

            for (listener in rules.filterIsInstance(RunCompletionListener::class.java)) {
                runListeners.add(listener)
            }
        }

        override fun apply(base: Statement, description: Description) = rules.fold(base) { b, r ->
            r.apply(b, description)
        }
    }
}

interface ClassCompletionListener {
    fun onClassComplete(clazz: Class<*>)
}

interface RunCompletionListener {
    fun onTestRunComplete()
}
