package ru.hse.spb

import ru.hse.spb.parser.ExpParser


data class Block(val statements: List<Statement>)

sealed class Statement
data class Function(val ident: Identifier, val params: List<Identifier>, val code: Block) : Statement()
data class Variable(val ident: Identifier, val expr: Expression = NotAnExpression) : Statement()
data class WhileLoop(val cond: Expression, val block: Block) : Statement()
data class IfCond(val cond: Expression, val ifBlock: Block, val elseBlock: Block? = null) : Statement()
data class Assignment(val ident: Identifier, val expr: Expression) : Statement()
data class ReturnExpr(val expr: Expression) : Statement()
object NotAStatement : Statement()


sealed class Expression : Statement()
data class FuncCall(val ident: Identifier, val arguments: List<Expression>) : Expression()
data class Identifier(val ident: String) : Expression()
data class Literal(val number: Int) : Expression()
data class BinOp(val left: Expression, val op: String, val right: Expression) : Expression()
object NotAnExpression : Expression()


fun nodeConverter(antlrType: ExpParser.BlockContext): Block {
    val statements = antlrType.statementGr().map(::nodeConverter)
    return Block(statements)

}

fun nodeConverter(antlrType: ExpParser.StatementGrContext): Statement {
    val isAssign = antlrType.assignment()
    val isFunc = antlrType.function()
    val isIf = antlrType.ifOp()
    val isWhile = antlrType.whileLoop()
    val isReturn = antlrType.returnInst()
    val isVar = antlrType.variable()
    val isExpr = antlrType.expression()

    return when {
        isAssign != null -> Assignment(Identifier(isAssign.IDENTIFIER().text), nodeConverter(isAssign.expression()))
        isFunc != null -> Function(Identifier(antlrType.function().IDENTIFIER().text),
                isFunc.parameterNames().IDENTIFIER().map{ el -> Identifier(el.text)},
                nodeConverter(isFunc.blockWithBraces().block()))
        isIf != null -> when (isIf.blockWithBraces().size) {
            1 -> IfCond(nodeConverter(isIf.expression()),
                    nodeConverter(isIf.blockWithBraces(0).block()))
            else -> IfCond(nodeConverter(isIf.expression()),
                    nodeConverter(isIf.blockWithBraces(0).block()),
                    nodeConverter(isIf.blockWithBraces(1).block()))
        }
        isVar != null -> Variable(Identifier(isVar.IDENTIFIER().text),
                nodeConverter(isVar.expression()))
        isWhile != null -> WhileLoop(nodeConverter(isWhile.expression()), nodeConverter(isWhile.blockWithBraces().block()))
        isReturn != null -> ReturnExpr(nodeConverter(isReturn.expression()))
        isExpr != null -> nodeConverter(isExpr)
        else -> NotAStatement
    }
}

fun nodeConverter(antlrType: ExpParser.ExpressionContext): Expression {

    val isFcall = antlrType.functionCall()
    val isIdent = antlrType.IDENTIFIER()
    val isLiteral = antlrType.LITERAL()

    if (isFcall != null) {
        val args = mutableListOf<Expression>()
        for (i: ExpParser.ExpressionContext in isFcall.arguments().expression()) {
            args.add(nodeConverter(i))
        }
        return FuncCall(Identifier(isFcall.IDENTIFIER().text), args)
    } else if (isIdent != null) {
        return Identifier(isIdent.text)

    } else if (isLiteral != null) {
        return Literal(isLiteral.text.toInt())
    } else if (antlrType.left != null) { //is Binop
        return BinOp(nodeConverter(antlrType.left),
                antlrType.op.text,
                nodeConverter(antlrType.right))
    } else {
        return NotAnExpression
    }

}
