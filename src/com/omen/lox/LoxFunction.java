package com.omen.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
	private final Stmt.Function decleration;
	private final Environment closure;

	LoxFunction(Stmt.Function decleration, Environment closure) {
		this.decleration = decleration;
		this.closure = closure;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> args) {
		Environment env = new Environment(this.closure);

		for (int i = 0; i < decleration.params.size(); i++) {
			env.define(decleration.params.get(i).lexeme, args.get(i));
		}

		try {
			interpreter.executeBlock(decleration.body, env);
		} catch (Return returnValue) {
			return returnValue.value;
		}

		return null;
	}

	@Override
	public int arity() {
		return this.decleration.params.size();
	}

	@Override
	public String toString() {
		return "<fn " + this.decleration.name.lexeme + ">";
	}
}
