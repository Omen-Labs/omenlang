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

		T visitFunctionStmt(Function function);

		T visitReturnStmt(Return retrunstmt);

		T visitClassStmt(Class classStmt);
	}

	static class Class extends Stmt {

		Class(Token name, List<Stmt.Function> methods) {
			this.name = name;
			this.methods = methods;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitClassStmt(this);
		}

		final Token name;
		final List<Stmt.Function> methods;
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
		// The initializer is essentially the value binding to the variable name.
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

	static class Return extends Stmt {
		Return(Token keyword, Expr value) {

			this.keyword = keyword;
			this.value = value;
		}

		@Override
		<T> T accept(Stmt.Visitor<T> visitor) {
			return visitor.visitReturnStmt(this);
		}

		final Token keyword;
		final Expr value;
	}

	static class Function extends Stmt {
		Function(Token name, List<Token> params, List<Stmt> body) {

			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		<T> T accept(Stmt.Visitor<T> visitor) {
			return visitor.visitFunctionStmt(this);
		}

		final Token name;
		final List<Token> params;
		final List<Stmt> body;
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
