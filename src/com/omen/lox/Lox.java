package com.omen.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	private static final Interpreter interpreter = new Interpreter();

	public static void main(String[] args) throws IOException {

		if (args.length > 1) {
			System.out.println("Usage: jlox [script]");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}

	}

	private static void runFile(String path) throws IOException {
		if (!path.endsWith(".lox")) {
			System.err.println("File type not supported.\nFile must end with .lox");
			System.exit(65);
		}
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));

		// Error exit code
		if (hadError)
			System.exit(65);
		if (hadRuntimeError)
			System.exit(70);
	}

	// Repl impl
	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if (line == null)
				break;
			run(line);
			hadError = false;
		}
	}

	static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}

	public static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		Parser parser = new Parser(tokens);
		List<Stmt> stmts = parser.parse();

		if (hadError)
			return;

		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(stmts);

		if (hadError)
			return;

		interpreter.interpret(stmts);
	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String messages) {
		System.out.printf("[line %d] Error %s: %s\n", line, where, messages);
	}

	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, "at end", message);
		} else {
			report(token.line, " at " + token.lexeme + "'", message);
		}
	}

}
