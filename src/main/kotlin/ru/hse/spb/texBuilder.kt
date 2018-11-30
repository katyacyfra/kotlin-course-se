package ru.hse.spb

import java.io.OutputStream

interface Output {
    fun append(toAppend: String)

}

class OutputStringBuilder(private val stringBuilder: StringBuilder): Output {
    override fun append(toAppend: String) {
        stringBuilder.append(toAppend)
    }
    override fun toString():String {
        return stringBuilder.toString()
    }
}

class OutputToStream(private val stream: OutputStream): Output {
    override fun append(toAppend: String) {
        stream.write(toAppend.toByteArray())
    }
}



interface Element {
    fun render(builder: Output, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: Output, indent: String) {
        builder.append("$indent$text\n")
    }
}

infix fun String.to(value: String): String {
    return "$this=$value"
}


@DslMarker
annotation class TexCommandMarker

@TexCommandMarker
abstract class OneCommand(val name: String, private val argument: String, vararg val parameters: String) : Element {
    override fun render(builder: Output, indent: String) {
        builder.append("$indent\\$name${renderParams(*parameters)}{$argument}\n")
    }

    fun renderParams(vararg parameters: String): String {
        return if (parameters.isEmpty()) "" else parameters.joinToString(separator = ",", prefix = "[", postfix = "]")

    }

    fun toOutputStream(stream: OutputStream) {
        render(OutputToStream(stream), "")
    }

}

@TexCommandMarker
abstract class Block(name: String, vararg params: String) : OneCommand(name, "", *params) {
    protected val children = arrayListOf<Element>()

    override fun render(builder: Output, indent: String) {
        builder.append("$indent\\begin{$name}${renderParams(*parameters)}\n")
        renderChildren(builder, indent)
        builder.append("$indent\\end{$name}\n")
    }

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children += tag
        return tag
    }

    fun renderChildren(builder: Output, indent: String, child: ArrayList<Element> = children, trim: String = "  ") {
        for (ch in child) {
            ch.render(builder, indent + trim)
        }
    }

    operator fun String.unaryPlus() {
        children += TextElement(this)
    }

    fun frame(frameTitle: String, vararg params: String, init: Frame.() -> Unit) = initTag(Frame(frameTitle, *params), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun alignment(type: AlignmentValue, init: Alignment.() -> Unit) = initTag(Alignment(type), init)

    fun math(text: String) = children.add(Math(text))

    fun customTag(name: String, vararg params: String, init: CustomTag.() -> Unit) = initTag(CustomTag(name, *params), init)

}

class Document(vararg params: String) : Block("document", *params) {
    private val header = arrayListOf<Element>()
    private var docClassCounter = 0

    override fun render(builder: Output, indent: String) {
        when (docClassCounter) {
            0 -> throw IllegalArgumentException("Document should have documentclass comand!")
            1 -> {
                renderChildren(builder, indent, header, "")
                super.render(builder, indent)
            }
            else -> throw IllegalArgumentException("Document should have only one documentclass comand!")
        }
    }


    fun documentClass(argument: String, vararg params: String) {
        header.add(DocumentClass(argument, *params))
        docClassCounter++
    }

    fun usepackage(argument: String, vararg params: String) = header.add(UsePackage(argument, *params))
}


class DocumentClass(arg: String, vararg params: String) : OneCommand("documentclass", arg, *params)

class UsePackage(arg: String, vararg params: String) : OneCommand("usepackage", arg, *params)

class Frame(private val frameTitle: String, vararg params: String) : Block("frame", *params) {
    override fun render(builder: Output, indent: String) {
        builder.append("$indent\\$name${renderParams(*parameters)}" +
                "\\frametitle{$frameTitle}\n")
        builder.append("$indent\\begin{$name}\n")
        renderChildren(builder, indent)
        builder.append("$indent\\end{$name}\n")
    }
}

class Itemize : Block("itemize") {
    fun item(init: Item.() -> Unit): Item = initTag(Item(), init)
}

class Enumerate : Block("enumerate") {
    fun item(init: Item.() -> Unit): Item = initTag(Item(), init)
}

class Item : Block("item") {
    override fun render(builder: Output, indent: String) {
        builder.append("$indent\\item")
        renderChildren(builder, " ", children, "")
    }
}

class Math(private val text: String) : Block("") {
    override fun render(builder: Output, indent: String) {
        builder.append("$indent\$$text\$\n")
    }
}

enum class AlignmentValue(val value: String) {
    FLUSHLEFT("flushleft"),
    CENTER("center"),
    FLUSHRIGHT("flushright")
}

class Alignment(type: AlignmentValue) : Block(type.value)

class CustomTag(name: String, vararg params: String) : Block(name, *params)

fun document(init: Document.() -> Unit): Document {
    return Document().apply(init)
}