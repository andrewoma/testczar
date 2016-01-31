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

import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.*

/**
 * Executes the `before` and `after` functions around each test function
 */
class AroundTest(val before: () -> Unit = {}, val after: () -> Unit = {}) : ExternalResource() {
    override fun before() {
        before.invoke()
    }

    override fun after() {
        after.invoke()
    }
}

/**
 * Executes the `before` and `after` functions once for all tests in a class
 */
class AroundTestClass(val id: String = "", val before: () -> Unit = {}, val after: () -> Unit = {}) : ExternalResource(), ClassCompletionListener {
    companion object {
        private val executed = hashMapOf<Class<*>, MutableSet<String>>()
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val execute = synchronized(executed) {
                    executed.getOrPut(description.testClass) { hashSetOf() }.add(id)
                }
                if (execute) {
                    before.invoke()
                }
                base.evaluate()
            }
        }
    }

    override fun onClassComplete(clazz: Class<*>) {
        val execute = synchronized(executed) {
            executed[clazz]?.remove(id) ?: false
        }
        if (execute) {
            after.invoke()
        }
    }
}

/**
 * Executes the `before` and `after` functions once for all tests with the same `id`
 * Note: `after` is only invoked at the end of the entire test run as there is
 */
class AroundAll(val id: Any, val before: () -> Unit = {}, val after: () -> Unit = {}) : ExternalResource(), RunCompletionListener {
    companion object {
        private val executed = HashSet<Any>()
    }

    override fun before() {
        val execute = synchronized(executed) { executed.add(id) }
        if (execute) {
            before.invoke()
        }
    }

    override fun onTestRunComplete() {
        val execute = synchronized(executed) { executed.remove(id) }
        if (execute) {
            after.invoke()
        }
    }
}