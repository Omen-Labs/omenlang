package com.omen.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
	final Environment enclosing;
	private final Map<String, Object> values = new HashMap<>();

	void define(String name, Object value) {
		this.values.put(name, value);
	}

	Environment() {
		enclosing = null;
	}

	Environment(Environment encl) {
		this.enclosing = encl;
	}

	Object get(Token name) {
		if (this.values.containsKey(name.lexeme)) {
			if (this.values.get(name.lexeme) == null) {
				throw new RuntimeError(name, "Error: Variable '" + name.lexeme + "' not initialized");
			}
			return values.get(name.lexeme);
		}

		if (this.enclosing != null)
			return enclosing.get(name);

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	void assign(Token name, Object value) {
		if (values.containsKey(name.lexeme)) {
			values.put(name.lexeme, value);
			return;
		}

		if (this.enclosing != null) {
			this.enclosing.assign(name, value);
			return;
		}

		throw new RuntimeError(name, "Undefined varaible '" + name.lexeme + "'.");
	}
}
