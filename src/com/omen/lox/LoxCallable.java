package com.omen.lox;

import java.util.List;

interface LoxCallable {
	int arity();

	String toString();

	Object call(Interpreter interpreter, List<Object> arguments);

}
