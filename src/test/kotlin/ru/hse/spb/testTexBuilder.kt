package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalArgumentException

class TexBuilderTest {
    @Test
    fun testSimpleDocumentWithHeader() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())
    }

    @Test
    fun testManyPackages() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer", "slidestop", "dvips")
            usepackage("babel", "russian" /* varargs */)
            usepackage("beamerthemesplit")
            usepackage("pstricks")
        }
                .render(sb, "")
        val result = """
            |\documentclass[slidestop,dvips]{beamer}
            |\usepackage[russian]{babel}
            |\usepackage{beamerthemesplit}
            |\usepackage{pstricks}
            |\begin{document}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())
    }


    @Test(expected = IllegalArgumentException::class)
    fun testNoDocumentClass() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            usepackage("babel", "russian" /* varargs */)
        }
                .render(sb, "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testManyDocumentClass() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            documentClass("article")
            usepackage("babel", "russian" /* varargs */)
        }
                .render(sb, "")
    }

    @Test
    fun testSimpleFrame() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2", "arg3" to "arg4") {
                +"""It is just text in frame""".trimIndent()
            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2,arg3=arg4]\frametitle{frametitle}
            |  \begin{frame}
            |    It is just text in frame
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())

    }

    @Test
    fun testItemize() {
        val sb = OutputStringBuilder(StringBuilder())
        val rows = arrayOf("January", "February", "March")
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in rows) {
                        item { +row }
                    }
                }
            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2]\frametitle{frametitle}
            |  \begin{frame}
            |    \begin{itemize}
            |      \item January
            |      \item February
            |      \item March
            |    \end{itemize}
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())

    }

    @Test
    fun testCustomTag() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                customTag("pyglist", "language" to "kotlin") {
                    +"""To print 4 slides per page in acrobat click
                        |
                    """.trimMargin()
                }
            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2]\frametitle{frametitle}
            |  \begin{frame}
            |    \begin{pyglist}[language=kotlin]
            |      To print 4 slides per page in acrobat click
            |
            |    \end{pyglist}
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())

    }

    @Test
    fun testMath() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                math("\\lim_{x \\to \\infty} \\exp(-x) = 0")
            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2]\frametitle{frametitle}
            |  \begin{frame}
            |    $\lim_{x \to \infty} \exp(-x) = 0$
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())
    }

    @Test
    fun testAlignment() {
        val sb = OutputStringBuilder(StringBuilder())
        document {
            documentClass("beamer")
            usepackage("babel", "russian")
            frame("newframe", "arg1" to "arg2") {
                alignment(AlignmentValue.FLUSHLEFT) {
                    +"""Text align to left"""
                }
                alignment(AlignmentValue.FLUSHRIGHT) {
                    +"""Text align to right"""
                }
            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2]\frametitle{newframe}
            |  \begin{frame}
            |    \begin{flushleft}
            |      Text align to left
            |    \end{flushleft}
            |    \begin{flushright}
            |      Text align to right
            |    \end{flushright}
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())
    }

    @Test
    fun testAllTogether() {
        val sb = OutputStringBuilder(StringBuilder())
        val rows = arrayOf("January", "February", "March")
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                enumerate {
                    for (row in rows) {
                        item { +"$row month" }
                    }
                }
                customTag("pyglist", "language" to "kotlin") {
                    +"""To print 4 slides per page in acrobat click
                        |
                    """.trimMargin()
                }
            }
            frame("math") {
                alignment(AlignmentValue.CENTER) {
                    +"""Limits"""
                    math("\\lim_{x \\to \\infty} \\exp(-x) = 0")
                }

            }
        }
                .render(sb, "")
        val result = """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \frame[arg1=arg2]\frametitle{frametitle}
            |  \begin{frame}
            |    \begin{enumerate}
            |      \item January month
            |      \item February month
            |      \item March month
            |    \end{enumerate}
            |    \begin{pyglist}[language=kotlin]
            |      To print 4 slides per page in acrobat click
            |
            |    \end{pyglist}
            |  \end{frame}
            |  \frame\frametitle{math}
            |  \begin{frame}
            |    \begin{center}
            |      Limits
            |      ${'$'}\lim_{x \to \infty} \exp(-x) = 0${'$'}
            |    \end{center}
            |  \end{frame}
            |\end{document}
        """.trimMargin()
        assertEquals(result, sb.toString().trimIndent())

    }
}