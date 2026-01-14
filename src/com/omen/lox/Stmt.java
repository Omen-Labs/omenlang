package com.omen.lox;

import java.util.List;

abstract class Stmt{

abstract <T> T accept(Visitor<T>);

interface <T> Visitor<T> {
T visitVarStmt;
T visitExpressionStmt;
T visitPrintStmt;

}

static class Var extends Stmt {
Var(Token name, Expr initializer) {
		this.name = name;
		this.initializer = initializer;
		}

		final Token name;
		final Expr initializer;
		}
static class Expression extends Stmt {
Expression(Expr Expression) {
		this.Expression = Expression;
		}

		final Expr Expression;
		}
static class Print extends Stmt {
Print(Expr expression) {
		this.expression = expression;
		}

		final Expr expression;
		}
}
