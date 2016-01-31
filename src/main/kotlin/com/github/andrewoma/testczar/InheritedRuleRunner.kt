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

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import java.lang.annotation.Inherited

/**
 * Like the standard jUnit Rule annotation, but inheritable
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Inherited
annotation public class InheritedRule

/**
 * A Runner that looks for inherited rules in addition to standard Rules
 */
class InheritableRuleRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {

    companion object {
        val lastFinished = ThreadLocal<Class<*>>()
    }

    override fun getTestRules(target: Any): MutableList<TestRule> {
        val result = super.getTestRules(target)

        result.addAll(testClass.getAnnotatedMethodValues(target, InheritedRule::class.java, TestRule::class.java))
        result.addAll(testClass.getAnnotatedFieldValues(target, InheritedRule::class.java, TestRule::class.java))

        return result
    }

    override fun run(notifier: RunNotifier) {
        notifier.addListener(object : RunListener() {
            override fun testFinished(description: Description) {
                lastFinished.set(description.testClass)
            }

            override fun testRunFinished(result: Result?) {
                for (completionListener in TestBase.runListeners.reversed()) {
                    completionListener.onTestRunComplete()
                }
            }
        })
        super.run(notifier)
    }
}