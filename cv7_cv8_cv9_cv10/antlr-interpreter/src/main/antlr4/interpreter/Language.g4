grammar Language;

program: statement+ ;

statement
    : primitiveType IDENTIFIER (',' IDENTIFIER)* ';' # declaration
    | expr ';'                                       # printExpr
    ;

expr: expr op=(MUL|DIV|MOD) expr                # mulDiv
    | expr op=(ADD|SUB) expr                # addSub
    | INT                                   # int
    | IDENTIFIER                            # id
    | FLOAT                                 # float
    | '(' expr ')'                          # parens
    | <assoc=right> IDENTIFIER '=' expr     # assignment
    ;

primitiveType
    : type=INT_KEYWORD
    | type=FLOAT_KEYWORD
    ;


INT_KEYWORD : 'int';
FLOAT_KEYWORD : 'float';
SEMI:               ';';
COMMA:              ',';
MUL : '*' ; 
DIV : '/' ;
MOD : '%' ;
ADD : '+' ;
SUB : '-' ;
IDENTIFIER : [a-zA-Z]+ ; 
FLOAT : [0-9]+'.'[0-9]+ ;
INT : [0-9]+ ; 
WS : [ \t\r\n]+ -> skip ;
