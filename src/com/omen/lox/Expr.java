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
// primary    -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")";

abstract class Expr {

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitBinaryExpr(Binary expr);

		T visitGroupingExpr(Grouping expr);

		T visitLiteralExpr(Literal expr);

		T visitUnaryExpr(Unary expr);
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
