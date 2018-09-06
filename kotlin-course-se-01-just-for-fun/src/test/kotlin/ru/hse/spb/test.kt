package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testOne() {
        val text = "<table><tr><td></td></tr></table>".toCharArray()
        val solution = TagCounter(text, text.size)
        solution.readTable()
        assertEquals("1", solution.output())
    }

    @Test
    fun testTwo() {
        val text = "<table>" +
                "<tr>" +
                "<td>" +
                "<table><tr><td></td></tr><tr><td></" +
                "td" +
                "></tr><tr" +
                "><td></td></tr><tr><td></td></tr></table>" +
                "</td>" +
                "</tr>" +
                "</table>"
        val chars = text.toCharArray()
        val solution = TagCounter(chars, chars.size)
        solution.readTable()
        assertEquals("1 4", solution.output())
    }

    @Test
    fun testThree() {
        val text = "<table><tr><td>" +
                "<table><tr><td>" +
                "<table><tr><td>" +
                "<table><tr><td></td><td></td>" +
                "</tr><tr><td></td></tr></table>" +
                "</td></tr></table>" +
                "</td></tr></table>" +
                "</td></tr></table>"
        val chars = text.toCharArray()
        val solution = TagCounter(chars, chars.size)
        solution.readTable()
        assertEquals("1 1 1 3", solution.output())
    }
}