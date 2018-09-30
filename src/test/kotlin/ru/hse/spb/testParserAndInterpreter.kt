package ru.hse.spb


import org.junit.Test
import java.io.ByteArrayOutputStream
import java.text.ParseException
import org.junit.After
import org.junit.Assert.assertEquals
import java.io.PrintStream
import org.junit.Before


class testParserInterpreter {

    private val outContent = ByteArrayOutputStream()
    private val originalOut = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(originalOut)
    }

    @Test(expected = ParseException::class)
    fun testSyntaxErrors() {
        val source = "fun wrong bad" +
                "}"
        interpret(source)
    }

    @Test
    fun testSimple() {
        val source = "var x = 1" +
                "var y = 2" +
                "println(x + y*y)"
        interpret(source)
        assertEquals("5\n", outContent.toString())
    }

    @Test
    fun testIf() {
        val source = "var a = 10\n" +
                "var b = 20\n" +
                "if (a > b) {\n" +
                "    println(1)\n" +
                "} else {\n" +
                "    println(0)\n" +
                "}"
        interpret(source)
        assertEquals("0\n", outContent.toString())
    }

    @Test
    fun testFib() {
        val source = "fun fib(n) {\n" +
                "    if (n <= 1) {\n" +
                "        return 1\n" +
                "    }\n" +
                "    return fib(n - 1) + fib(n - 2)\n" +
                "}\n" +
                "\n" +
                "var i = 1\n" +
                "while (i <= 5) {\n" +
                "    println(i, fib(i))\n" +
                "    i = i + 1\n" +
                "}"
        interpret(source)
        assertEquals("1\n1\n2\n2\n3\n3\n4\n5\n5\n8\n", outContent.toString())
    }

    @Test
    fun testScope() {
        val source = "fun foo(n) {\n" +
                "    fun bar(m) {\n" +
                "        return m + n\n" +
                "    }\n" +
                "\n" +
                "    return bar(1)\n" +
                "}\n" +
                "\n" +
                "println(foo(41)) // prints 42"
        interpret(source)
        assertEquals("42\n", outContent.toString())

    }

    @Test
    fun testLocalVar() {
        val source = "var x = 2\n" +
                "fun test(x) {\n" +
                "return x\n}" +
                "println(test(3))"
        interpret(source)
        assertEquals("3\n", outContent.toString())

    }

    @Test
    fun testRewriteLocalVar() {
        val source = "var x = 2\n" +
                "fun test(x) {\n" +
                "x = x + 1\n}" +
                "test(x)\n" +
                "println(x)"
        interpret(source)
        assertEquals("2\n", outContent.toString())
    }

}