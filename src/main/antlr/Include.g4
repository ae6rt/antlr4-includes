grammar Include;

prog:   include+ ;

include: '#include' '"' ID '"' ';' # includeExpr
    ;

ID  :   [a-zA-Z.]+ ;      // match identifiers
WS  :   [ \n\t]+ -> channel(1) ;
OTHER  :  [^a-zA-Z.]+ -> channel(1) ;
