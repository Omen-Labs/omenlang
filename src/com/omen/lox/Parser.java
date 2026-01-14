package com.omen.lox;

import java.util.ArrayList;
import java.util.List;
import static com.omen.lox.TokenType.*;

class Parser {

	private static class ParseError extends RuntimeException {
	};

	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	private Expr expression() {
		// TODO: sort out if there is any conflict with comments

		//
		if (this.tokens.get(0).type == EOF)
			return null;
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

	private Stmt declaration() {
		try {
			if (this.match(VAR))
				return varDeclaration();

			return statement();
		} catch (ParseError e) {
			synchronize();
			return null;
		}
	}

	private Stmt varDeclaration() {
		Token name = consume(IDENTIFIER, "Expect varaible name");

		Expr initializer = null;
		if (this.match(EQUAL))
			initializer = expression();

		consume(SEMICOLON, "Expect ';' after variable declaration");
		return new Stmt.Var(name, initializer);
	}

	// TODO: well it will be implemented soon anyways and that is handeling a parse
	// error.
	List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();

		while (!isAtEnd()) {
			statements.add(this.declaration());
		}
		return statements;
	}

	private Stmt printStatement() {
		Expr value = expression();
		consume(SEMICOLON, "Expect ';' after value.");
		return new Stmt.Print(value);
	}

	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(SEMICOLON, "Expect ';' after value.");
		return new Stmt.Expression(expr);
	}

	private Stmt statement() {
		if (this.match(PRINT))
			return printStatement();
		// TODO: fix this line.
		return expressionStatement();
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

		if (match(IDENTIFIER))
			return new Expr.Variable(this.previous());

		if (this.match(NUMBER, STRING)) {
			return new Expr.Literal(previous().literal);
		}

		if (match(LEFT_PAREN)) {
			Expr expr = this.expression();
			this.consume(RIGHT_PAREN, "Expect ')' after expression.");

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
