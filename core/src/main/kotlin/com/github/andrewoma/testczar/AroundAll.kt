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

import com.github.andrewoma.testczar.internal.RunCompletionListener
import org.junit.rules.ExternalResource
import java.util.*

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