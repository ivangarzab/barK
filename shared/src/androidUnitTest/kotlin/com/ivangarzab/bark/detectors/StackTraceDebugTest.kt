package com.ivangarzab.bark.detectors

import org.junit.Test

/**
 * Debug test to see what's actually in the stack trace
 * This will help us understand why getCallerTag() is returning "Unknown"
 */
class StackTraceDebugTest {

    @Test
    fun `debug stack trace contents`() {
        val stackTrace = Thread.currentThread().stackTrace

        println("=== FULL STACK TRACE ===")
        stackTrace.forEachIndexed { index, element ->
            println("[$index] ${element.className}.${element.methodName}")
        }

        println("\n=== FILTERED ANALYSIS ===")
        stackTrace.forEachIndexed { index, element ->
            val className = element.className
            val passes = shouldIncludeClass(className)
            println("[$index] $passes | $className")
        }

        println("\n=== WHAT getCallerTag() WOULD RETURN ===")
        val result = getCallerTag()
        println("Result: '$result'")
    }

    private fun shouldIncludeClass(className: String): Boolean {
        return !className.endsWith("Bark") &&
                !className.contains("Trainer") &&
                !className.contains("TagDetection") &&
                !className.contains("TestDetection") &&
                !className.startsWith("java.") &&
                !className.startsWith("android.") &&
                !className.startsWith("kotlin.") &&
                !className.startsWith("dalvik.") &&
                !className.startsWith("jdk.") &&
                !className.startsWith("org.junit") &&
                !className.contains("junit.runners") &&
                !className.contains("gradle") &&
                !className.contains("Reflective") &&
                !className.contains("Framework") &&
                !className.contains("Runner") &&
                !className.contains("Callable") &&
                !className.contains("Method.invoke") &&
                !className.contains("Proxy") &&
                !className.contains("$\$") &&
                !className.contains("Lambda") &&
                className.isNotBlank()
    }
}