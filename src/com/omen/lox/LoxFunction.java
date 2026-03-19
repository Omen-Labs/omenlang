package com.omen.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
	private final Stmt.Function decleration;
	private final Environment closure;
	private final boolean isInitializer;

	LoxFunction(Stmt.Function decleration, Environment closure, boolean isInitializer) {
		this.decleration = decleration;
		this.closure = closure;
		this.isInitializer = isInitializer;
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
			if (isInitializer)
				return this.closure.getAt(0, "this");
			return returnValue.value;
		}

		if (isInitializer)
			return closure.getAt(0, "this");
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

	LoxFunction bind(LoxInstance instance) {
		Environment environment = new Environment(closure);
		environment.define("this", instance);

		return new LoxFunction(this.decleration, environment, this.isInitializer);
	}

}
