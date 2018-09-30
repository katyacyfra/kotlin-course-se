package ru.hse.spb

import java.security.InvalidParameterException


class Evaluator constructor(block: Block,
                            scope: MutableMap<String, Int> = mutableMapOf(),
                            functionMap: MutableMap<String, Block> = mutableMapOf(),
                            functionParameters: MutableMap<String, MutableList<Identifier>> = mutableMapOf()) {

    private val mainBlock: Block = block
    private var result: Int? = null
    private var blockScope: MutableMap<String, Int> = scope
    private val funcMap = functionMap
    private val funcParam = functionParameters


    fun runEvaluation() {
        evaluate(mainBlock)
    }

    private fun boolToInt(b: Boolean): Int {
        if (b) {
            return 1
        } else {
            return 0
        }
    }


    private fun intToBool(i: Int): Boolean {
        return (i != 0)
    }

    private fun getVarValue(varname: String): Int {
        val value = blockScope.get(varname)
        if (value != null) {
            return value
        } else {
            throw NoSuchElementException("Unknown variable $varname")
        }

    }

    private fun evaluate(value: Block) {
        for (s: Statement in value.statements) {
            if (result == null) {
                evaluate(s)
            }
        }
    }

    private fun evaluate(node: Statement) {
        when (node) {
            is Variable -> evaluate(node)
            is Assignment -> evaluate(node)
            is IfCond -> evaluate(node)
            is ReturnExpr -> evaluate(node)
            is Function -> evaluate(node)
            is WhileLoop -> evaluate(node)
            is Expression -> evaluate(node)
            else -> println("No such statement")
        }
    }

    private fun evaluate(node: Variable) {
        if (node.expr != NotAnExpression) {
            blockScope.put(evaluate(node.ident), evaluate(node.expr))
        } else {
            blockScope.put(evaluate(node.ident), 0)
        }
    }

    private fun evaluate(node: Assignment) {
        val varname = evaluate(node.ident)
        if (blockScope.containsKey(varname)) {
            blockScope.put(varname, evaluate(node.expr))
        } else {
            throw NoSuchElementException("Unknown variable $varname")
        }
    }

    private fun evaluate(node: IfCond) {
        if (intToBool(evaluate(node.cond))) {
            evaluate(node.ifBlock)
        } else {
            if (node.elseBlock != null) {
                evaluate(node.elseBlock)
            }
        }
    }

    private fun evaluate(node: ReturnExpr) {
        result = evaluate(node.expr)
    }

    private fun evaluate(node: WhileLoop) {
        while (intToBool(evaluate(node.cond))) {
            evaluate(node.block)
        }
    }

    private fun evaluate(node: Expression): Int {
        when (node) {
            is FuncCall -> return evaluate(node)
            is BinOp -> return evaluate(node)
            is Identifier -> return getVarValue(evaluate(node))
            is Literal -> return evaluate(node)
            else -> return 0
        }
    }

    private fun evaluate(node: Function) {
        val funcName = evaluate(node.ident)
        funcMap.put(funcName, node.code)
        funcParam.put(funcName, node.params)
    }

    private fun evaluate(node: BinOp): Int {
        val left = evaluate(node.left)
        val right = evaluate(node.right)
        val op = node.op
        when (op) {
            "+" -> return left + right
            "-" -> return left - right
            "*" -> return left * right
            "/" -> if (right != 0) return left / right else throw ArithmeticException()
            "%" -> return left % right
            ">" -> return boolToInt(left > right)
            "<" -> return boolToInt(left < right)
            ">=" -> return boolToInt(left >= right)
            "<=" -> return boolToInt(left <= right)
            "==" -> return boolToInt(left == right)
            "!=" -> return boolToInt(left != right)
            "&&" -> return boolToInt(intToBool(left) && intToBool(right))
            "||" -> return boolToInt(intToBool(left) || intToBool(right))
            else -> return 0
        }
    }

    private fun evaluate(node: Identifier): String {
        return node.ident
    }


    private fun evaluate(value: Literal): Int {
        return value.number
    }

    private fun evaluate(node: FuncCall): Int {
        val fname = evaluate(node.ident)
        val code = funcMap.get(fname)
        val args = node.arguments
        if (fname.equals("println")) {
            for (a in args) {
                when (a) {
                    is Identifier -> println(getVarValue(evaluate(a)))
                    else -> println(evaluate(a))
                }
            }
            return 0
        }
        if (code != null) {
            val params = funcParam.get(fname)
            if (params != null) {
                if (params.size != args.size) {
                    throw InvalidParameterException("params != args number in function $fname call!")
                } else {
                    //prepare function scope
                    val scope = mutableMapOf<String, Int>()
                    scope.putAll(blockScope)

                    var i = 0
                    while (i < args.size) {
                        scope.put(evaluate(params[i]), evaluate(args[i]))
                        i++
                    }
                    val bl = Evaluator(code, scope, funcMap, funcParam)
                    bl.evaluate(code)
                    return bl.result ?: 0


                }
            } else {
                throw InvalidParameterException("Invalid params in function $fname call!")
            }

        } else {
            return 0
        }

    }

    fun getResult(): Int? {
        return result
    }

}

