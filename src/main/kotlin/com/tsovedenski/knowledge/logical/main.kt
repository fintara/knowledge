package com.tsovedenski.knowledge.logical

import com.tsovedenski.knowledge.logical.problems.AnalysisProblem
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

/**
 * Created by Tsvetan Ovedenski on 2019-04-09.
 */
fun <T> T.println() = also { println(it) }

fun Int.bits(): List<Boolean> = bits(32)
fun Int.bits(length: Int): List<Boolean> = (0 until length).map { 1 == ((this shr it) and 1) }

fun Int.pow(exp: Int) = Math.pow(this.toDouble(), exp.toDouble()).roundToInt()

fun main() {
//    val ap = AnalysisProblem(plantKnowledge) { create { ref(1) and ref(6) } }
//    val ap = AnalysisProblem(driverKnowledge) { create { !ref(1) and ref(3) } }
//    val dmp = DecisionMakingProblem(driverKnowledge) { create { !ref(2) and !ref(4) } }
//    val solution = dmp.solve()
//
//    solution.pretty().println()
//
//    plantKnowledge.facts.forEach { println("F${it.identifier} = ${it.expr.pretty()}") }

    val ap = AnalysisProblem(randomKnowledge) { !ref(1) }

//    val decomposed = ap.decompose()
//    decomposed.forEachIndexed { index, layer ->
//        if (layer.facts.isNotEmpty()) {
//            println("F_$index=(${layer.facts.map { it.identifier }.joinToString(",")})")
//        }
//        println("alpha_$index=(${layer.variables.map { it.identifier }.joinToString(",")})")
//    }

    lateinit var solution: Expr
    val time = measureTimeMillis {
        solution = ap.solve()
    }
    solution.pretty().println()
    println("Took $time ms")
}

data class Fact(val identifier: Int, val expr: Expr)

fun List<Fact>.disj() = reduce { acc, fact ->
    Fact(
        acc.identifier + fact.identifier,
        Expr.Or(acc.expr, fact.expr)
    )
}
fun List<Fact>.conj() = reduce { acc, fact ->
    Fact(
        acc.identifier + fact.identifier,
        Expr.And(acc.expr, fact.expr)
    )
}

data class Knowledge(
    val facts: List<Fact>,
    val variables: List<Expr.Variable>,
    val inputs: List<Expr.Variable>,
    val outputs: List<Expr.Variable>
)