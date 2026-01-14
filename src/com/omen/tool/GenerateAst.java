//package com.omen.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.out.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}

		String outputDir = args[0];

		defineAst(outputDir, "Expr", Arrays.asList(
				"Literal : Object value",
				"Unary : Token operator, Expr right",
				"Binary : Expr left, Token operator, Expr right",
				"Assing: Token name, Expr value",
				"Var : Token name, Expr initializer"));

		defineAst(outputDir, "Stmt", Arrays.asList(
				"Var: Token name, Expr initializer",
				"Expression: Expr Expression",
				"Print: Expr expression"));
	}

	private static void defineAst(
			String outputDir,
			String baseName,
			List<String> types) throws IOException {

		String path = outputDir + "/" + baseName + ".java";
		PrintWriter write = new PrintWriter(path, "UTF-8");

		write.println("package com.omen.lox;");
		write.println();
		write.println("import java.util.List;");
		write.println();
		write.println("abstract class " + baseName + "{");

		write.println();

		write.println("abstract <T> T accept(Visitor<T>);");

		write.println();
		write.println("interface Visitor<T> {");

		for (String type : types) {
			String subcls = type.split(":")[0].trim();

			write.println("T visit" + subcls + baseName + "(" + subcls + subcls.toLowerCase() + ");");

		}

		write.println();
		write.println("}");
		write.println();

		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();

			defineType(write, baseName, className, fields);
		}

		write.println("}");
		write.close();
	}

	private static void defineType(
			PrintWriter write,
			String baseName,
			String className,
			String fieldList) {

		write.println("static class " + className + " extends " + baseName + " {");

		// Contructor
		write.println("" + className + "(" + fieldList + ") {");

		// Store params in fields.

		String[] fields = fieldList.split(", ");

		for (String field : fields) {

			String name = field.split(" ")[1];
			write.println("		this." + name + " = " + name + ";");

		}

		write.println("		}");

		write.println();
		for (String field : fields) {
			write.println("		final " + field + ";");
		}

		write.println("		}");

	}

}
