package com.omen.lox;

import java.util.List;

import static com.omen.lox.TokenType.*;

class Parser {

	private static class ParseError extends RuntimeException {
	}

	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	private Expr expression() {
		return this.equality();
	}

	private Expr equality() {
		Expr expr = this.comparisson();

		while (this.match(BANG_EQUAL, EQUAL_EQUAL)) {

			Token operator = this.previous();
			Expr right = this.comparisson();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	Expr parse() {
		try {
			return expression();
		} catch (ParseError e) {
			return null;
		}
	}

	private Expr comparisson() {
		Expr expr = this.term();

		while (match(GREATER_EQUAL, GREATER, LESS, LESS_EQUAL)) {
			Token operator = this.previous();
			Expr right = this.term();

			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = this.factor();

		while (this.match(MINUS, PLUS)) {
			Token operator = this.previous();
			Expr right = this.factor();

			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = this.unary();

		while (this.match(SLASH, STAR)) {
			Token operator = this.previous();
			Expr right = this.unary();

			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {

		if (this.match(BANG, MINUS)) {
			Token operator = this.previous();
			Expr right = this.unary();

			return new Expr.Unary(operator, right);
		}

		return this.primary();
	}

	private Expr primary() {
		if (this.match(FALSE))
			return new Expr.Literal(false);
		if (this.match(TRUE))
			return new Expr.Literal(true);
		if (this.match(NIL))
			return new Expr.Literal(null);

		if (this.match(NUMBER, STRING)) {
			return new Expr.Literal(previous().literal);
		}

		if (match(LEFT_PAREN)) {
			Expr expr = this.expression();
			consume(RIGHT_PAREN, "Expect ')' after expression.");

			return new Expr.Grouping(expr);
		}

		throw error(peek(), "Expect expression");

	}

	private boolean match(TokenType... types) {

		for (TokenType type : types) {
			if (this.check(type)) {
				this.advance();
				return true;
			}
		}

		return false;

	}

	private boolean check(TokenType type) {
		if (this.isAtEnd())
			return false;
		return this.peek().type == type;
	}

	private Token advance() {
		if (!this.isAtEnd())
			current++;
		return this.previous();
	}

	private boolean isAtEnd() {
		return this.peek().type == EOF;
	}

	private Token peek() {
		return this.tokens.get(this.current);
	}

	private Token previous() {
		return this.tokens.get(this.current - 1);
	}

	private Token consume(TokenType type, String message) {
		if (this.check(type))
			return this.advance();

		throw this.error(peek(), message);
	}

	private ParseError error(Token token, String message) {
		Lox.error(token, message);
		return new ParseError();
	}

	private void synchronize() {
		this.advance();

		while (!isAtEnd()) {

			if (this.previous().type == SEMICOLON)
				return;

			switch (peek().type) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
				default:
					break;
			}

			this.advance();
		}
	}

}
