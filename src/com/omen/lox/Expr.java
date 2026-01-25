package com.omen.lox;

import java.util.List;

// Literals - Numbers, strings, bools, and nil;
//
// Unary expr - A prefix ! to perform logical not, and - to negate a #;
//
// Binary expr - The infix arhtmetic and logical operators;
//
// Parentheses - A pair of parentheses ();

// ** This is the structure **
// expression -> literal | unary | binary | grouping
//
// literal -> NUMBER | STRING | "true" | "false" | "nil";
//
// grouping -> "(" expression ")";
//
// unary -> ( "-" | "!" ) expression;
//
// binary -> expression operator expression
//
// operator -> "==" | "!=" | "<" | ">" | "<=" 
// 		| ">=" | "+" | "-" | "*" | "/";

// **The associativity graph: **
// Equality -> Left
// Comparison -> Left
// Term -> Left
// Factor -> Left
// Unary -> Left

// ** This is our parsing rules **: 
//
// expr       -> equality;
// equality   -> comparison ( ( "!=" | "==" ) comparison )* ;
// comparison ->  term ( ( ">" | ">=" | "<" | "<=" )term )* ;
// term       -> factor ( ( "-" | "+" ) factor )* ;
// factor     -> unary ( ( "/" | "*" ) )* ;
// unary      -> ( "!" | "-" ) unary | primary;
// primary    -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER;

// ** Rules as defined in chapter 8 **
//
// program   -> statetemt* EOF;
// statement -> exprStmt | printStmt;
// exprStmt  -> expression ";";
// printStmt -> "print" expression ";";

// ** Rykes fir types of statements **
//
// program     -> declaration* EOF;
// decleration -> varDecl | statement;
// statement   -> exprStmt | printStmt;
// varDecl     -> "var" IDENTIFIER ( "=" expression )? ";" ;

// ** Blocks syntax and semantics
//
// statement -> exprStmt | printStmt | block;
// block     -> "{" declaration* "}" ;

// ** Cibdutuibak production **
//
// statement -> epxrStmt | ifStmt | printStmt | block;
// ifStmt    -> "if" "(" expression ")" statement ( "else" statement )?;

// ** Logical expression **
//
// expression -> assignment;
// assignment -> IDENTIFIER "=" assignment | logic_or;
// logic_or   -> logic_and ( "or" logic_and )*;
// lofic_and  -> equility ( "and" equility )*;

// ** While Loops **
//
// statement -> exprStmt | ifStmt | printStmt | whileStmt | block;
// whileStmt -> "while" "(" expression ")" staement;

// ** For Loops **
//
// statement -> exprStmt | forStmt | ifStmt | printStmt | whileStmt | block;
// forStmt   -> "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement;

// ** Functions ** 
//
// unary     -> ( "!" | "-" ) unary | call;
// call      -> primary ( "(" arguments? ")")*;
// arguments -> expression ( "," expression )*;
abstract class Expr {

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitBinaryExpr(Binary expr);

		T visitGroupingExpr(Grouping expr);

		T visitLiteralExpr(Literal expr);

		T visitUnaryExpr(Unary expr);

		T visitVariableExpr(Variable var);

		T visitAssignExpr(Assign asgn);

		T visitLogicalExpr(Logical logical);

		T visitCallExpr(Call logical);

	}

	static class Logical extends Expr {
		Logical(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		final Expr left;
		final Token operator;
		final Expr right;

		@Override
		<T> T accept(Expr.Visitor<T> visitor) {
			return visitor.visitLogicalExpr(this);
		}
	}

	static class Call extends Expr {
		Call(Expr callee, Token paren, List<Expr> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		final Expr callee;
		final Token paren;
		final List<Expr> arguments;

		@Override
		<T> T accept(Expr.Visitor<T> visitor) {
			return visitor.visitCallExpr(this);
		}
	}

	static class Assign extends Expr {
		Assign(Token name, Expr value) {
			this.name = name;
			this.value = value;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitAssignExpr(this);
		}

		final Token name;
		final Expr value;
	}

	static class Variable extends Expr {
		final Token name;

		Variable(Token name) {
			this.name = name;
		}

		@Override
		<T> T accept(Expr.Visitor<T> visitor) {
			return visitor.visitVariableExpr(this);
		}
	}

	static class Binary extends Expr {

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		final Expr left;
		final Token operator;
		final Expr right;
	}

	static class Grouping extends Expr {

		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		final Expr expression;
	}

	static class Literal extends Expr {

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		final Object value;
	}

	static class Unary extends Expr {
		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		final Token operator;
		final Expr right;
	}

}
