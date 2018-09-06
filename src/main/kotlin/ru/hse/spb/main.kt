//  http://codeforces.com/contest/51/problem/B
//  B. Анализ таблиц bHTML
//  http://codeforces.com/contest/51/submission/42588052

package ru.hse.spb

import java.util.Scanner

class TagCounter(
        private val text: CharArray,
        private val len: Int
) {
    private var result = mutableListOf<Int>()
    private var currentPos: Int = 0

    private fun readTag(): String? {
        val result = mutableListOf<Char>()
        while (currentPos < len && text[currentPos] != '>') {
            result.add(text[currentPos])
            currentPos = currentPos + 1
        }
        if (currentPos == len) {
            return null
        }
        result.add(text[currentPos])
        currentPos = currentPos + 1
        return result.joinToString("")

    }

    fun readTable() {
        var counter: Int = 0
        var currentTag = readTag()
        while (currentTag != "</table>" && currentTag != null) {
            if (currentTag == "<tr>") {
                counter = counter + readTr()
            }
            currentTag = readTag()
        }
        result.add(counter)
    }

    private fun readTr(): Int {
        var counter: Int = 0
        var currentTag = readTag()
        while (currentTag != "</tr>" && currentTag != null) {
            if (currentTag == "<td>") {
                readTd()
                counter = counter + 1
            }
            currentTag = readTag()
        }
        return counter
    }

    private fun readTd() {
        var currentTag = readTag()
        if (currentTag == "<table>") {
            readTable()
        }
    }

    fun output(): String {
        result.sort()
        return (result.joinToString(" "))
    }

}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val lines = mutableListOf<String>()
    while (input.hasNextLine()) {
        lines.add(input.nextLine());
    }
    val text = lines.joinToString("").toCharArray()
    val solution = TagCounter(text, text.size)
    solution.readTable()
    println(solution.output())
}