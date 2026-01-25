package com.omen.lox;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	private Environment env = new Environment();
	private int timesVisited = 0;

	void interpret(List<Stmt> stmts) {
		try {
			for (Stmt stmt : stmts) {
				if (stmt == null)
					return;
				execute(stmt);
			}
		} catch (RuntimeError e) {
			Lox.runtimeError(e);
		}
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		if (isTruthy(evaluate(stmt.condition))) {
			execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			execute(stmt.elseBranch);
		}

		return null;
	}

	@Override
	public Object visitLogicalExpr(Expr.Logical expr) {
		Object left = this.evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			if (this.isTruthy(left))
				return left;
		} else {
			if (!this.isTruthy(left))
				return left;
		}

		return this.evaluate(expr.right);
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		while (isTruthy(evaluate(stmt.condtion))) {
			execute(stmt.body);
		}

		return null;
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		Object value = evaluate(expr.value);

		this.env.assign(expr.name, value);
		return value;
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		// System.out.println("visited : " + ++this.timesVisited);
		executeBlock(stmt.stmts, new Environment(this.env));
		return null;
	}

	void executeBlock(List<Stmt> stmts, Environment env) {
		Environment prev = this.env;

		try {
			this.env = env;

			for (Stmt stmt : stmts) {
				this.execute(stmt);
			}

		} finally {
			this.env = prev;
		}
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = this.evaluate(stmt.initializer);
		}

		env.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Object visitVariableExpr(Expr.Variable variable) {
		return env.get(variable.name);
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return this.evaluate(expr.expression);
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) throws RuntimeException {
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case BANG:
				return !isTruthy(right);
			case MINUS:
				this.checkNumberOperand(expr.operator, right);
				return -(double) right;
			default:
				throw new RuntimeException("Operator mishandled");
		}

	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) throws RuntimeException {
		Object right = this.evaluate(expr.right);
		Object left = this.evaluate(expr.left);

		switch (expr.operator.type) {
			case MINUS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left - (double) right;
			case STAR:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left * (double) right;
			case SLASH:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left / (double) right;
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				} else {
					return String.format("%s %s", left, right);
				}
			case COMMA:

			case GREATER:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left > (double) right;
			case GREATER_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left >= (double) right;
			case LESS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left < (double) right;
			case LESS_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left <= (double) right;
			case EQUAL_EQUAL:
				return this.isEqual(left, right);
			case BANG_EQUAL:
				return !this.isEqual(left, right);
			default:
				throw new RuntimeException("Operatator mishandled");
		}

	}

	private String stringify(Object obj) {
		if (obj == null)
			return "nil";

		if (obj instanceof Double) {
			String text = obj.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}

			return text;
		}

		return obj.toString();
	}

	private void checkNumberOperand(Token operator, Object expr) {
		if (expr instanceof Double)
			return;
		throw new RuntimeError(operator, "Operand must be a number");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (right instanceof Double && (double) right == 0)
			throw new RuntimeError(operator, "Division by zero");

		if (left instanceof Double && right instanceof Double)
			return;
		throw new RuntimeError(operator, "Operands must be a number");
	}

	private boolean isEqual(Object lexpr, Object rexpr) {

		if (lexpr == null && rexpr == null)
			return true;
		if (lexpr == null)
			return false;

		return lexpr.equals(rexpr);

	}

	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	private boolean isTruthy(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Boolean)
			return (boolean) obj;
		if (obj instanceof Double && (double) obj == 0.0)
			return false;

		return true;

	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}

}
