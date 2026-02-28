package com.omen.lox;

import java.util.List;

// This class is the runtime representation of a ** Class **
class LoxClass implements LoxCallable {
	final String name;

	LoxClass(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		LoxInstance instance = new LoxInstance(this);
		return instance;
	}

	// This arity method returns the amount of parameters
	@Override
	public int arity() {
		return 0;
	}
}
