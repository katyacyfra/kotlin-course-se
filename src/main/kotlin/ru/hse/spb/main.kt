//  http://codeforces.com/contest/51/problem/B
//  B. Анализ таблиц bHTML
//  http://codeforces.com/contest/51/submission/42588052

package ru.hse.spb

import java.util.Scanner

class TagCounter(
        private val text: CharArray
) {
    private val result = mutableListOf<Int>()
    private var currentPosition: Int = 0
    private val length: Int = text.size
    private var answer = ""

    private fun readTag(): String? {
        val result = StringBuilder()
        while (currentPosition < length && text[currentPosition] != '>') {
            result.append(text[currentPosition])
            currentPosition++
        }
        if (currentPosition == length) {
            return null
        }
        result.append(text[currentPosition])
        currentPosition++
        return result.toString()

    }

    private fun readTable() {
        var counter: Int = 0
        var currentTag = readTag()
        while (currentTag != "</table>" && currentTag != null) {
            if (currentTag == "<tr>") {
                counter += countCellsInRow()
            }
            currentTag = readTag()
        }
        result.add(counter)
    }

    private fun countCellsInRow(): Int {
        var counter: Int = 0
        var currentTag = readTag()
        while (currentTag != "</tr>" && currentTag != null) {
            if (currentTag == "<td>") {
                readTd()
                counter++
            }
            currentTag = readTag()
        }
        return counter
    }

    private fun readTd() {
        if (readTag() == "<table>") {
            readTable()
        }
    }

    fun output(): String {
        return answer
    }

    private fun clear() {
        currentPosition = 0
        result.clear()
    }

    fun run() {
        readTable()
        result.sort()
        answer = result.joinToString(" ")
        clear()
    }
}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val lines = StringBuilder()
    while (input.hasNextLine()) {
        lines.append(input.nextLine());
    }
    val text = lines.toString().toCharArray()
    val solution = TagCounter(text)
    solution.run()
    println(solution.output())
}