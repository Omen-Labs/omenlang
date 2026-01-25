package com.omen.lox;

import java.util.List;

abstract class Stmt {

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);

		T visitPrintStmt(Print stmt);

		T visitVarStmt(Var var);

		T visitBlockStmt(Block block);

		T visitIfStmt(If ifsexpr);

		T visitWhileStmt(While ifsexpr);
	}

	static class Var extends Stmt {

		Var(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitVarStmt(this);
		}

		final Token name;
		final Expr initializer;
	}

	static class If extends Stmt {
		If(Expr condition, Stmt thenBranch, Stmt elseBranch) {

			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		<T> T accept(Stmt.Visitor<T> visitor) {
			return visitor.visitIfStmt(this);
		}

		final Expr condition;
		final Stmt thenBranch;
		final Stmt elseBranch;
	}

	static class Block extends Stmt {

		final List<Stmt> stmts;

		@Override
		<T> T accept(Stmt.Visitor<T> visitor) {
			return visitor.visitBlockStmt(this);
		}

		Block(List<Stmt> stmt) {
			this.stmts = stmt;
		}
	}

	static class Expression extends Stmt {

		Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStmt(this);
		}

		final Expr expression;
	}

	static class While extends Stmt {

		While(Expr condition, Stmt body) {
			this.condtion = condition;
			this.body = body;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitWhileStmt(this);
		}

		final Expr condtion;
		final Stmt body;
	}

	static class Print extends Stmt {

		Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitPrintStmt(this);
		}

		final Expr expression;
	}
}
