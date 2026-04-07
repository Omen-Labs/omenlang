package com.omen.lox;

import java.util.List;
import java.util.Map;

// This class is the runtime representation of a ** Class **
class LoxClass implements LoxCallable {
	final String name;
	private final Map<String, LoxFunction> methods;
	final LoxClass superclass;

	LoxFunction findMethod(String name) {
		if (methods.containsKey(name)) {
			return methods.get(name);
		}

		if (this.superclass != null) {
			return superclass.findMethod(name);
		}

		return null;
	}

	LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
		this.name = name;
		this.methods = methods;
		this.superclass = superclass;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		LoxInstance instance = new LoxInstance(this);

		LoxFunction initializer = findMethod("init");
		if (initializer != null) {
			initializer.bind(instance).call(interpreter, arguments);
		}
		return instance;
	}

	// This arity method returns the amount of parameters
	@Override
	public int arity() {
		LoxFunction initializer = findMethod("init");
		if (initializer == null)
			return 0;
		return initializer.arity();
	}
}
