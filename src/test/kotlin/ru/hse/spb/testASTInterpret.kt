import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.*
import ru.hse.spb.Function
import java.security.InvalidParameterException


class testASTInterpreter {

    private fun eval(statements: MutableList<Statement>, scope: MutableMap<String, Int> = mutableMapOf()): Int? {
        val block = Block(statements)
        val evaluator = Evaluator(block, scope)
        evaluator.runEvaluation()
        return evaluator.getResult()

    }

    @Test
    fun testSimpleNodeReturn() {
        val statements = mutableListOf<Statement>()
        statements.add(ReturnExpr(Literal(42)))
        assertEquals(42, eval(statements))
    }

    @Test
    fun testSimpleNodeReturnNull() {
        val statements = mutableListOf<Statement>()
        statements.add(Variable(Identifier("x")))
        assertEquals(null, eval(statements))

    }

    @Test
    fun testBlockScope() {
        val blockScope = mutableMapOf<String, Int>()
        blockScope.put("x", 1024)
        val statements = mutableListOf<Statement>()
        statements.add(ReturnExpr(Identifier("x")))
        assertEquals(1024, eval(statements, blockScope))
    }

    @Test(expected = NoSuchElementException::class)
    fun testBlockScopeNoSuchVariable() {
        val blockScope = mutableMapOf<String, Int>()
        blockScope.put("x", 1024)
        val statements = mutableListOf<Statement>()
        statements.add(ReturnExpr(Identifier("y")))
        eval(statements, blockScope)
    }

    @Test
    fun testVariable() {
        val statements = mutableListOf<Statement>()
        statements.add(Variable(Identifier("x"), Literal(3)))
        statements.add(Variable(Identifier("x"), BinOp(Literal(2), "+", Identifier("x"))))
        statements.add(ReturnExpr(Identifier("x")))
        assertEquals(5, eval(statements))
    }

    @Test
    fun testWhile() {
        val statements = mutableListOf<Statement>()
        val loopStatements = mutableListOf<Statement>()
        loopStatements.add(Variable(Identifier("i"), BinOp(Identifier("i"), "+", Literal(1))))
        statements.add(Variable(Identifier("i"), Literal(0)))
        statements.add(WhileLoop(BinOp(Identifier("i"), "<", Literal(5)), Block(loopStatements)))
        statements.add(ReturnExpr(Identifier("i")))
        assertEquals(5, eval(statements))
    }

    @Test
    fun testFuncCall() {
        val statements = mutableListOf<Statement>()
        val functionStatements = mutableListOf<Statement>()
        val params = mutableListOf<Identifier>()
        params.add(Identifier("x"))
        params.add(Identifier("y"))
        params.add(Identifier("z"))
        val args = mutableListOf<Expression>()
        args.add(Literal(1))
        args.add(Literal(2))
        args.add(Literal(3))
        functionStatements.add(ReturnExpr(
                BinOp(Identifier("x"), "+", BinOp(Identifier("y"), "*", Identifier("z"))))
        )
        statements.add(Function(Identifier("test"), params, Block(functionStatements)))
        statements.add(ReturnExpr(FuncCall(Identifier("test"), args)))
        assertEquals(7, eval(statements))
    }

    @Test
    fun testPrintln() {
        val statements = mutableListOf<Statement>()
        val args = mutableListOf<Expression>()
        args.add(Literal(1))
        args.add(Identifier("x"))
        args.add(BinOp(Literal(22), "%", Literal(13)))
        statements.add(Variable(Identifier("x"), BinOp(Literal(22), "<", Literal(13))))
        statements.add(FuncCall(Identifier("println"), args))
        eval(statements)
    }

    @Test(expected = InvalidParameterException::class)
    fun badFuncCall() {
        val statements = mutableListOf<Statement>()
        val args = mutableListOf<Expression>()
        val functionStatements = mutableListOf<Statement>()
        args.add(Literal(1))
        args.add(Literal(2))
        functionStatements.add(ReturnExpr(
                BinOp(Identifier("x"), "+", BinOp(Identifier("y"), "*", Identifier("z"))))
        )
        val params = mutableListOf<Identifier>()
        params.add(Identifier("x"))
        params.add(Identifier("y"))
        params.add(Identifier("z"))
        statements.add(Function(Identifier("test"), params, Block(functionStatements)))
        statements.add(ReturnExpr(FuncCall(Identifier("test"), args)))
        eval(statements)

    }

    @Test
    fun testRecursiveCall() {
        val statements = mutableListOf<Statement>()
        val params = mutableListOf<Identifier>()
        params.add(Identifier("n"))
        val funcStatements = mutableListOf<Statement>()
        val ifStatements = mutableListOf<Statement>()

        val paramFibOne = mutableListOf<Expression>()
        paramFibOne.add(BinOp(Identifier("n"), "-", Literal(1)))
        val paramFibTwo = mutableListOf<Expression>()
        paramFibTwo.add(BinOp(Identifier("n"), "-", Literal(2)))
        ifStatements.add(ReturnExpr(Literal(1)))
        funcStatements.add(IfCond(BinOp(Identifier("n"), "<=", Literal(1) ),
                Block(ifStatements)))
        funcStatements.add(ReturnExpr(BinOp(FuncCall(Identifier("fib"), paramFibOne), "+",
                FuncCall(Identifier("fib"), paramFibTwo))))
        statements.add(Function(Identifier("fib"), params, Block(funcStatements)))

        val args = mutableListOf<Expression>()
        args.add(Literal(6))
        statements.add(ReturnExpr(FuncCall(Identifier("fib"), args)))
        assertEquals(13, eval(statements))

    }
}
