package ru.hse.spb

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer


class ErrorListener : BaseErrorListener() {
    private val errors: MutableList<String> = mutableListOf()

    override fun syntaxError(recognizer: Recognizer<*, *>?,
                             offendingSymbol: Any?,
                             line: Int,
                             charPos: Int,
                             msg: String?,
                             e: RecognitionException?) {
        errors.add("Syntax error at line $line. Message: $msg")
    }

    fun getSyntaxErrors(): List<String> {
        return errors.toList()
    }

}