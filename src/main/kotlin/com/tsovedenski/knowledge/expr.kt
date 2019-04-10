package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-10.
 */
sealed class Expr {
    data class Value(val value: Boolean) : Expr()
    data class Variable(val identifier: Int, val label: String) : Expr()
    data class Implies(val head: Expr, val tail: Expr) : Expr()
    data class And(val left: Expr, val right: Expr) : Expr()
    data class Or(val left: Expr, val right: Expr) : Expr()
    data class Not(val expr: Expr) : Expr()
}

fun Expr.eval(env: Map<Int, Boolean>): Boolean = when (this) {
    is Expr.Value -> value
    is Expr.Variable -> env.getValue(identifier)
    is Expr.Implies -> if (head.eval(env)) tail.eval(env) else true
    is Expr.And -> left.eval(env) && right.eval(env)
    is Expr.Or -> left.eval(env) || right.eval(env)
    is Expr.Not -> !expr.eval(env)
}

infix fun Expr.implies(tail: Expr) = Expr.Implies(this, tail)
infix fun Expr.and(right: Expr) = Expr.And(this, right)
infix fun Expr.or(right: Expr) = Expr.Or(this, right)
operator fun Expr.not() = Expr.Not(this)