package com.omen.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omen.lox.TokenType.*;

class Scanner {

	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<String, TokenType>();

		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);

	}

	private final String source;

	private final List<Token> tokens = new ArrayList<>();

	// First char in the lexeme
	private int start = 0;
	// The char currnelty being considered
	private int current = 0;

	private int line = 1;

	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			this.start = this.current;
			scanToken();
		}

		this.tokens.add(new Token(EOF, "", null, line));
		return this.tokens;

	}

	private boolean isAtEnd() {
		return this.current >= this.source.length();
	}

	private void scanToken() {

		// TODO: advance() still consumes chars that are not valid this is on purpose so
		// the users can see all their errors at once
		char c = advance();

		switch (c) {
			case '(':
				this.addToken(LEFT_PAREN);
				break;
			case ')':
				this.addToken(RIGHT_PAREN);
				break;
			case '{':
				this.addToken(LEFT_BRACE);
				break;
			case '}':
				this.addToken(RIGHT_BRACE);
				break;
			case ',':
				this.addToken(COMMA);
				break;
			case '.':
				this.addToken(DOT);
				break;
			case '-':
				this.addToken(MINUS);
				break;
			case '+':
				this.addToken(PLUS);
				break;
			case ';':
				this.addToken(SEMICOLON);
				break;
			case '*':
				this.addToken(STAR);
				break;
			case '!':
				this.addToken(this.match('=') ? BANG_EQUAL : BANG);
				break;
			case '=':
				this.addToken(this.match('=') ? EQUAL_EQUAL : TokenType.EQUAL);
				break;
			case '>':
				this.addToken(this.match('=') ? GREATER_EQUAL : GREATER);
				break;
			case '<':
				this.addToken(this.match('=') ? LESS_EQUAL : LESS);
				break;
			case '/':
				if (this.match('/')) {

					while (this.peek() != '\n' && !this.isAtEnd())
						this.advance();
				} else if (this.match('*')) {
					// Implementing multiline comment

					while (!this.isAtEnd()) {
						if (this.match('\n')) {
							this.line++;
						}

						if (this.match('*')) {
							if (this.match('/')) {
								break;
							}
						}

					}

				} else {
					this.addToken(SLASH);
				}

				break;
			case ' ':
			case '\r':
			case '\t':
				break;
			case '\n':
				this.line++;
				break;
			case '"':
				this.string();
				break;
			default:

				// TODO: I think i will implement this myself its not too difficult
				if (this.isDigit(c)) {
					this.number();
				} else if (this.isAlpha(c)) {
					this.identifier();
				} else {
					// TODO: understand this error things since what happens if its a literal
					Lox.error(line, "Unexpected character");
				}
				break;

		}
	}

	// Move to next char
	private char advance() {
		this.current++;
		return this.source.charAt(this.current - 1);
	}

	// Wrapper
	private void addToken(TokenType type) {
		this.addToken(type, null);
	}

	// Append token to tokens
	private void addToken(TokenType type, Object literal) {
		String text = this.source.substring(this.start, this.current);
		this.tokens.add(new Token(type, text, literal, line));
	}

	private boolean match(char expected) {
		if (this.isAtEnd())
			return false;
		if (this.source.charAt(this.current) != expected)
			return false;

		this.current++;
		return true;
	}

	private char peek() {
		if (this.isAtEnd())
			return '\0';
		return this.source.charAt(this.current);
	}

	private void string() {

		while (this.peek() != '"' && !this.isAtEnd()) {
			if (this.peek() == '\n')
				line++;
			this.advance();
		}

		// TODO: Determine if the language allows for multiline strings: PS-> I dont
		// think it does.
		if (isAtEnd()) {
			Lox.error(line, "Unterminated string.");
			return;
		}

		// This is for the closing "
		this.advance();

		// This extracts the string literal within the ""
		String value = this.source.substring(this.start + 1, this.current - 1);
		this.addToken(STRING, value);
	}

	// Characters.isDigit() allows for numbers we dont want
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private void number() {

		while (this.isDigit(this.peek()))
			this.advance();

		if (this.peek() == '.' && this.isDigit(this.peekNext())) {
			advance();
			while (this.isDigit(this.peek()))
				this.advance();
		}

		addToken(NUMBER, Double.parseDouble(this.source.substring(this.start, this.current)));
	}

	private char peekNext() {
		if (this.current + 1 >= source.length())
			return '\0';
		return this.source.charAt(this.current + 1);
	}

	private boolean isAlpha(char potentialAlpha) {
		return (potentialAlpha >= 'a' && potentialAlpha <= 'z')
				|| (potentialAlpha >= 'A' && potentialAlpha <= 'Z')
				|| potentialAlpha == '_';
	}

	private void identifier() {
		while (this.isAlphaNumeric(this.peek()))
			this.advance();

		String text = this.source.substring(this.start, this.current);
		TokenType type = keywords.get(text);
		if (type == null)
			type = IDENTIFIER;

		addToken(type);
	};

	private boolean isAlphaNumeric(char c) {
		return this.isAlpha(c) || this.isDigit(c);
	}

}
