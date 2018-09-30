package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.File
import java.io.InputStream
import java.text.ParseException

fun interpret(source: String) {
    val listener = ErrorListener()

    val expLexer = ExpLexer(CharStreams.fromString(source))
    val parser = ExpParser(BufferedTokenStream(expLexer))
    parser.addErrorListener(listener)
    expLexer.addErrorListener(listener)

    val block = parser.block()

    val errors = listener.getSyntaxErrors()
    if (errors.size == 0) {
        val mainBlock = nodeConverter(block)
        Evaluator(mainBlock).runEvaluation()
    }
    else {
        for (e in errors) {
            println(e)
        }
        throw ParseException("Parsing failed!", 1)
    }

}


fun main(args: Array<String>) {
    val inputStream: InputStream = File(args[0]).inputStream()
    val source = inputStream.bufferedReader().use { it.readText() }
    interpret(source)

}