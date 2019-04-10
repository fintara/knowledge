package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-09.
 */
fun main() {
    println(cakeKnowledge)
}

data class Fact(val expr: Expr)

data class Knowledge(
    val facts: List<Fact>,
    val variables: List<Expr.Variable>,
    val inputProperties: List<Int>,
    val outputProperties: List<Int>
)