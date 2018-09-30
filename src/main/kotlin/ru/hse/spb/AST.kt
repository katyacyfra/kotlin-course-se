package ru.hse.spb

import ru.hse.spb.parser.ExpParser


data class Block(val statements: MutableList<Statement>)

sealed class Statement
data class Function(val ident: Identifier, val params: MutableList<Identifier>, val code: Block) : Statement()
data class Variable(val ident: Identifier, val expr: Expression = NotAnExpression) : Statement()
data class WhileLoop(val cond: Expression, val block: Block) : Statement()
data class IfCond(val cond: Expression, val ifBlock: Block, val elseBlock: Block? = null) : Statement()
data class Assignment(val ident: Identifier, val expr: Expression) : Statement()
data class ReturnExpr(val expr: Expression) : Statement()
object NotAStatement : Statement()


sealed class Expression : Statement()
data class FuncCall(val ident: Identifier, val arguments: MutableList<Expression>) : Expression()
data class Identifier(val ident: String) : Expression()
data class Literal(val number: Int) : Expression()
data class BinOp(val left: Expression, val op: String, val right: Expression) : Expression()
object NotAnExpression : Expression()


fun nodeConverter(antlrType: ExpParser.BlockContext): Block {
    val statements = mutableListOf<Statement>()
    for (item: ExpParser.StatementGrContext in antlrType.statementGr()) {
        statements.add(nodeConverter(item))
    }
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

    if (isAssign != null) {
        return Assignment(Identifier(isAssign.IDENTIFIER().text),
                nodeConverter(isAssign.expression()))
    } else if (isFunc != null) {
        val params = mutableListOf<Identifier>()
        for (i in isFunc.parameterNames().IDENTIFIER()) {
            params.add(Identifier(i.text))
        }
        return Function(Identifier(antlrType.function().IDENTIFIER().text),
                params,
                nodeConverter(isFunc.blockWithBraces().block()))
    } else if (isIf != null) {
        when (isIf.blockWithBraces().size) {
            1 -> return IfCond(nodeConverter(isIf.expression()),
                    nodeConverter(isIf.blockWithBraces(0).block()))
            else -> return IfCond(nodeConverter(isIf.expression()),
                    nodeConverter(isIf.blockWithBraces(0).block()),
                    nodeConverter(isIf.blockWithBraces(1).block()))
        }
    } else if (isVar != null) {
        return Variable(Identifier(isVar.IDENTIFIER().text),
                nodeConverter(isVar.expression()))
    } else if (isWhile != null) {
        return WhileLoop(nodeConverter(isWhile.expression()),
                nodeConverter(isWhile.blockWithBraces().block()))
    } else if (isReturn != null) {
        return ReturnExpr(nodeConverter(isReturn.expression()))
    } else if (isExpr != null) {
        return nodeConverter(isExpr)
    } else {
        return NotAStatement
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
