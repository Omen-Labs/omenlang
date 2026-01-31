package com.omen.lox;

import java.util.ArrayList;
import java.util.Arrays;
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
		return this.assignment();
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

	private Expr or() {
		Expr expr = this.and();

		while (match(OR)) {
			Token operator = previous();
			Expr right = this.and();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr and() {
		Expr expr = this.equality();

		while (this.match(AND)) {
			Token operator = this.previous();
			Expr right = equality();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr assignment() {
		Expr expr = this.or();

		if (match(EQUAL)) {
			Token equals = this.previous();
			Expr value = this.assignment();

			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable) expr).name;
				return new Expr.Assign(name, value);
			}

			error(equals, "Invalid asignment target.");
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

	private Stmt whileStatement() {
		consume(LEFT_PAREN, "Expect '(' after  'while'");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after  'while'");

		Stmt body = statement();

		return new Stmt.While(condition, body);

	}

	private Stmt forStatement() {
		consume(LEFT_PAREN, "Expect '(' after 'for'");

		Stmt initializer;
		if (match(SEMICOLON)) {
			initializer = null;
		} else if (match(VAR)) {
			initializer = varDeclaration();
		} else {
			initializer = expressionStatement();
		}

		Expr condition = null;
		if (!check(SEMICOLON)) {
			condition = expression();
		}
		consume(SEMICOLON, "Expect ';' after loop condition");

		Expr increment = null;
		if (!check(RIGHT_PAREN)) {
			increment = expression();
		}
		consume(RIGHT_PAREN, "Expect ')' after loop condition");

		Stmt body = statement();

		if (increment != null) {
			body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
		}

		if (condition == null)
			condition = new Expr.Literal(true);
		body = new Stmt.While(condition, body);

		if (initializer != null) {
			body = new Stmt.Block(Arrays.asList(initializer, body));
		}

		return body;

	}

	// Parameter to distinguish between functions and methods.
	private Stmt.Function function(String kind) {
		Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
		consume(LEFT_PAREN, "Expect '(' after " + kind + " name");
		List<Token> parameters = new ArrayList<>();

		if (!check(RIGHT_PAREN)) {
			do {
				if (parameters.size() >= 255)
					error(peek(), "Can't have more tahn 255 params");

				parameters.add(consume(IDENTIFIER, "Expect parameter name."));
			} while (match(COMMA));
		}

		consume(RIGHT_PAREN, "Expect ')' after params");

		consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
		List<Stmt> body = this.block();
		return new Stmt.Function(name, parameters, body);

	}

	private Stmt returnStmt() {
		Token keyword = previous();
		Expr value = null;
		if (!check(SEMICOLON)) {
			value = expression();
		}

		consume(SEMICOLON, "Expect ';' after return value.");
		return new Stmt.Return(keyword, value);
	}

	private Stmt statement() {
		if (this.match(FUN))
			return function("function");
		if (this.match(FOR))
			return forStatement();
		if (this.match(WHILE))
			return whileStatement();
		if (this.match(IF))
			return ifStatement();
		if (this.match(PRINT))
			return printStatement();
		if (this.match(RETURN))
			return returnStmt();
		if (this.match(LEFT_BRACE))
			return new Stmt.Block(this.block());
		// TODO: fix this line.
		return expressionStatement();
	}

	private Stmt ifStatement() {
		consume(LEFT_PAREN, "Expect '(' after if");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after if condition");

		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		if (match(ELSE)) {
			elseBranch = statement();
		}

		return new Stmt.If(condition, thenBranch, elseBranch);
	}

	private List<Stmt> block() {
		List<Stmt> stmts = new ArrayList<>();

		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			stmts.add(declaration());
		}

		consume(RIGHT_BRACE, "Expect '}' after block.");
		return stmts;
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

		return this.call();
	}

	private Expr call() {
		Expr expr = this.primary();

		while (true) {
			if (this.match(LEFT_PAREN))
				expr = this.finishCall(expr);
			else
				break;
		}

		return expr;
	}

	private Expr finishCall(Expr callee) {
		List<Expr> arguments = new ArrayList<>();
		if (!this.check(RIGHT_PAREN)) {
			do {
				if (arguments.size() >= 255) {
					this.error(peek(), "Can't have more than 255 arguments");
				}
				arguments.add(expression());
			} while (this.match(COMMA));
		}

		Token paren = this.consume(RIGHT_PAREN, "Expect ')' after arguments");

		return new Expr.Call(callee, paren, arguments);
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
