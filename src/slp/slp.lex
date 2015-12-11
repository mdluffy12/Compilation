package slp;
import java_cup.runtime.*;

%%

%cup
%class Lexer
%line
%scanerror RuntimeException

%{
	public int getLineNumber() { return yyline+1; }
%}

%{
	public String getText() { return yytext(); }
%}   
/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied letter to letter into the Lexer class code.                */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{   
    /*********************************************************************************/
    /* Create a new java_cup.runtime.Symbol with information about the current token */
    /*********************************************************************************/
    private Symbol token(int type)               {return new Symbol(type);}
    private Symbol token(int type, Object value) {return new Symbol(type, value);}
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator		= \r|\n|\r\n
WhiteSpace			= {LineTerminator} | [ \t\f]
NUMBER				= 00* | [1-9][0-9]*
IDENTIFIER			= [a-z][A-Za-z_0-9]*
CLASS_ID			= [A-Z][A-Za-z_0-9]*
QUOTE				= \"[\ -\! | #-\[ | \]-~ | \\\" | \\\\]*\"
COMMENT_ONE_LINE	= \/\/[^\n]*\n?
COMMENT_MULTI_LINE	= \/\* ([^\*] | \*[^\/])*\*? \*\/ 
   
/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/
   
/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/
   
<YYINITIAL> {
  
";"					{ return token(sym.SEMI);}
"+"					{ return token(sym.PLUS);}
"*"					{ return token(sym.MULTIPLY);}
"/"					{ return token(sym.DIVIDE);}
"-"					{ return token(sym.MINUS);}
"("					{ return token(sym.LP);}
")"					{ return token(sym.RP);}
"="					{  return token(sym.ASSIGN);}
"boolean"			{  return token(sym.BOOLEAN);}
"break"				{  return token(sym.BREAK);}
"class"				{  return token(sym.CLASS);}
","					{  return token(sym.COMMA);}
"continue"			{  return token(sym.CONTINUE);}
"\."				{  return token(sym.DOT);}
"=="				{  return token(sym.EQUAL);}
"extends"			{  return token(sym.EXTENDS);}
"else"				{  return token(sym.ELSE);}
"false"				{  return token(sym.FALSE);}
"true"				{  return token(sym.TRUE);}
">"					{  return token(sym.GT);}
">="				{  return token(sym.GTE);}
"if"				{  return token(sym.IF);}
"int"				{  return token(sym.INT);}
"&&"				{  return token(sym.LAND);}
"["					{  return token(sym.LB);}
"{"					{  return token(sym.LCBR);}
"length"			{  return token(sym.LENGTH);}
"new"				{  return token(sym.NEW);}
"!"					{  return token(sym.LNEG);}
"||"				{  return token(sym.LOR);}
"<"					{  return token(sym.LT);}
"<="				{  return token(sym.LTE);}
"%"					{  return token(sym.MOD);}
"!="				{  return token(sym.NEQUAL);}
"null"				{  return token(sym.NULL);}
"]"					{  return token(sym.RB);}
"}"					{  return token(sym.RCBR);}
"return"			{  return token(sym.RETURN);}
"static"			{  return token(sym.STATIC);}
"string"			{  return token(sym.STRING);}
"this"				{  return token(sym.THIS);}
"void"				{  return token(sym.VOID);}
"while"				{  return token(sym.WHILE);}

{QUOTE}				{
						return token(sym.QUOTE, new String(yytext()));
					}
{COMMENT_ONE_LINE}	{
					}
{COMMENT_MULTI_LINE} {
					}	
 
{CLASS_ID}			{
						return token(sym.CLASS_ID, new String(yytext()));
					}
					
					
{NUMBER}			{
						return token(sym.INTEGER, new Integer(yytext()));
					}   
{IDENTIFIER}		{
						return token(sym.ID, new String(yytext()));
					}
{WhiteSpace}		{ /* just skip what was found, do nothing */ }   

 . 					{ throw new RuntimeException("Illegal character at line " + (yyline+1) + " : '" + yytext() + "'"); }

<<EOF>> 			{ return new Token(yyline+1, "EOF", sym.EOF);}
}