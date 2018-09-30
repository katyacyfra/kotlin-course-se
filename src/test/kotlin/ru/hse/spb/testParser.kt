package ru.hse.spb


import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser


class testParser {

    private fun getBlock(input: String): ExpParser.BlockContext {
        val expLexer = ExpLexer(CharStreams.fromString(input))
        val parser = ExpParser(BufferedTokenStream(expLexer))
        return parser.block()
    }

    @Test
    fun testVarAssign() {
        val block = getBlock("var x = 3")
        assertEquals("x", block.statementGr().get(0).variable().IDENTIFIER().text)
        assertEquals("3", block.statementGr().get(0).variable().expression().LITERAL().text)
    }

    @Test
    fun testMultExpr() {
        val block = getBlock("i = (1 +2) + 3 - (9+0)")
        println(block.statementGr(0).assignment().expression().left.text)
    }

    @Test
    fun testVarComments() {
        val block = getBlock("var y // test")
        assertEquals("y", block.statementGr().get(0).variable().IDENTIFIER().text)
        assertEquals(1, block.statementGr().size) //comments were ignored
    }

    @Test
    fun testfCall() {
        val block = getBlock("println(foo(41)) // prints 42")
        println()
        val fcall = block.statementGr().get(0).expression().functionCall()
        assertEquals("println", fcall.IDENTIFIER().text)
        assertEquals("foo", fcall.arguments().expression().get(0).functionCall().IDENTIFIER().text)
        assertEquals("41", fcall.arguments().expression().get(0).functionCall().arguments().expression().get(0)
                .LITERAL().text)
        assertEquals(1, block.statementGr().size) //comments were ignored
    }

    @Test
    fun testIf() {
        val block = getBlock("var a = 10\n" +
                "var b = 20\n" +
                "if (a > b) {\n" +
                "    println(1)\n" +
                "} else {\n" +
                "    println(0)\n" +
                "}")
        assertEquals(3, block.statementGr().size)
        assertEquals("a", block.statementGr().get(0).variable().IDENTIFIER().text)
        assertEquals("b", block.statementGr().get(1).variable().IDENTIFIER().text)

        val ifSt = block.statementGr().get(2).ifOp()


        assertEquals("a", ifSt.expression().left.IDENTIFIER().text)
        assertEquals("b", ifSt.expression().right.IDENTIFIER().text)
        assertEquals(">", ifSt.expression().op.text)

        assertEquals(2, ifSt.blockWithBraces().size)
        assertEquals("println", ifSt.blockWithBraces().get(0).block().statementGr().get(0).expression()
                .functionCall().IDENTIFIER().text)
        assertEquals("println", ifSt.blockWithBraces().get(1).block().statementGr().get(0).expression()
                .functionCall().IDENTIFIER().text)

        assertEquals("1", ifSt.blockWithBraces().get(0).block().statementGr().get(0).expression()
                .functionCall().arguments().expression().get(0).LITERAL().text)
        assertEquals("0", ifSt.blockWithBraces().get(1).block().statementGr().get(0).expression()
                .functionCall().arguments().expression().get(0).LITERAL().text)

    }

    @Test
    fun testWhile() {
        val block = getBlock("while (i <= 5) {\n" +
                "    println(i, fib(i))\n" +
                "    i = i + 1\n" +
                "}")
        val whileSt = block.statementGr().get(0).whileLoop()

        assertEquals("i", whileSt.expression().left.IDENTIFIER().text)
        assertEquals("<=", whileSt.expression().op.text)
        assertEquals("5", whileSt.expression().right.LITERAL().text)

        val printExpr = whileSt.blockWithBraces().block().statementGr().get(0)
        val incrExpr = whileSt.blockWithBraces().block().statementGr().get(1)

        assertEquals("println", printExpr.expression().functionCall().IDENTIFIER().text)
        assertEquals("i", printExpr.expression().functionCall().arguments().expression().get(0)
                .IDENTIFIER().text)
        assertEquals("fib", printExpr.expression().functionCall().arguments().expression().get(1).functionCall()
                .IDENTIFIER().text)
        assertEquals("i", printExpr.expression().functionCall().arguments().expression().get(1).functionCall()
                .arguments().expression().get(0).IDENTIFIER().text)

        assertEquals("i", incrExpr.assignment().IDENTIFIER().text)
        assertEquals("i", incrExpr.assignment().expression().left.IDENTIFIER().text)
        assertEquals("+", incrExpr.assignment().expression().op.text)
        assertEquals("1", incrExpr.assignment().expression().right.LITERAL().text)

    }

    @Test
    fun testNestedFunc() {
        val block = getBlock("fun foo(n) {\n" +
                "    fun bar(m) {\n" +
                "        return m + n\n" +
                "    }\n" +
                "\n" +
                "    return bar(1)\n" +
                "}")

        assertEquals("foo", block.statementGr().get(0).function().IDENTIFIER().text)
        assertEquals(1, block.statementGr().get(0).function().parameterNames().IDENTIFIER().size)
        assertEquals("n", block.statementGr().get(0).function().parameterNames().IDENTIFIER().get(0).text)

        val funcInner = block.statementGr().get(0).function().blockWithBraces().block().statementGr()
        assertEquals("bar", funcInner.get(0).function().IDENTIFIER().text)
        assertEquals("bar", funcInner.get(1).returnInst().expression().functionCall().IDENTIFIER().text)

    }


}