/*
 * package com.omen.lox;
 * 
 * // TODO: Implement the Reverse polish notation.
 * 
 * class RPNPrinter implements Expr.Visitor<String> {
 * 
 * @Override
 * public String visitBinaryExpr(Expr.Binary expr) {
 * return groupLexemes(expr.operator.lexeme, expr.left, expr.right);
 * }
 * 
 * @Override
 * public String visitGroupingExpr(Expr.Grouping expr) {
 * return expr.accept(this);
 * }
 * 
 * @Override
 * public String visitLiteralExpr(Expr.Literal expr) {
 * if (expr.value == null)
 * return "nil";
 * return expr.value.toString();
 * }
 * 
 * @Override
 * public String visitUnaryExpr(Expr.Unary expr) {
 * return groupLexemes(expr.operator.lexeme, expr.right);
 * };
 * 
 * public String groupLexemes(String operator, Expr... exprs) {
 * StringBuilder currString = new StringBuilder();
 * 
 * for (Expr expr : exprs) {
 * currString.append(" ");
 * currString.append(expr.accept(this));
 * }
 * 
 * if (operator != "group") {
 * currString.append(operator);
 * }
 * 
 * return currString.toString();
 * }
 * 
 * }
 */
