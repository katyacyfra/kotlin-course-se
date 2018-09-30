grammar Exp;

/* FILE = BLOCK */
file     :    block EOF
         ;

//BLOCK = (STATEMENT)*
block     :    (statementGr)*
          ;

// STATEMENT = FUNCTION | VARIABLE | EXPRESSION | WHILE | IF | ASSIGNMENT | RETURN
statementGr     :    function | variable | expression | whileLoop | ifOp | assignment | returnInst
              ;

// FUNCTION = "fun" IDENTIFIER "(" PARAMETER_NAMES ")" BLOCK_WITH_BRACES
function     :    'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces
             ;

blockWithBraces     :    '{' block '}'
                    ;

//VARIABLE = "var" IDENTIFIER ("=" EXPRESSION)?
variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

//PARAMETER_NAMES = IDENTIFIER{,}
parameterNames     :   (IDENTIFIER (',' IDENTIFIER)*)?
                   ;

//WHILE = "while" "(" EXPRESSION ")" BLOCK_WITH_BRACES
whileLoop     :    'while' '(' expression ')' blockWithBraces
          ;

//IF = "if" "(" EXPRESSION ")" BLOCK_WITH_BRACES ("else" BLOCK_WITH_BRACES)?
ifOp     :    'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
       ;

// ASSIGNMENT = IDENTIFIER "=" EXPRESSION
assignment     :    IDENTIFIER '=' expression
               ;

//RETURN = "return" EXPRESSION
returnInst     :    'return' expression
           ;

/*
    Арифметическое выражение с операциями: +, -, *, /, %, >, <, >=, <=, ==, !=, ||, &&
    Семантика и приоритеты операций примерно как в Си
*/
//BINARY_EXPRESSION = <define-yourself>
//EXPRESSION = FUNCTION_CALL | BINARY_EXPRESSION | IDENTIFIER | LITERAL | "(" EXPRESSION ")"
expression     :    functionCall | IDENTIFIER | LITERAL | '(' expression ')'
                    |    <assoc=left> left=expression op=('*' | '/' | '%') right=expression
                    |    <assoc=left> left=expression op=('+' | '-') right=expression
                    |    left=expression op=('>' | '<' | '>=' | '<=' ) right=expression
                    |    left=expression op=('==' | '!=') right=expression
                    |left=expression op=('&&' | '||') right=expression
                    ;


//FUNCTION_CALL = IDENTIFIER "(" ARGUMENTS ")"
functionCall   :   IDENTIFIER '(' arguments ')'
               ;

//ARGUMENTS = EXPRESSION{","}
arguments     :    (expression (',' expression)*)?
              ;




/* Идентификатор как в Си */
//IDENTIFIER = <define-yourself>
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]* ;

/* Десятичный целочисленный литерал без ведущих нулей */
LITERAL    : '0' | '-'? ('1'..'9') ('0'..'9')* ;

WS : (' ' | '\t' | '\r'| '\n' | '//' .*? (('\r')?'\n' | EOF)) -> skip;