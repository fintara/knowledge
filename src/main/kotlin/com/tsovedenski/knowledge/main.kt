package com.tsovedenski.knowledge

import java.lang.Math.pow
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

/**
 * Created by Tsvetan Ovedenski on 2019-04-09.
 */
fun <T> T.println() = also { println(it) }

fun Int.bits(): List<Boolean> = (0..31).map { 1 == ((this shr it) and 1) }

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

    val ap = AnalysisProblem(randomKnowledge) { create { !ref(1) } }

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

fun List<Fact>.disj() = reduce { acc, fact -> Fact(acc.identifier + fact.identifier, Expr.Or(acc.expr, fact.expr)) }
fun List<Fact>.conj() = reduce { acc, fact -> Fact(acc.identifier + fact.identifier, Expr.And(acc.expr, fact.expr)) }

data class Knowledge(
    val facts: List<Fact>,
    val variables: List<Expr.Variable>,
    val inputs: List<Expr.Variable>,
    val outputs: List<Expr.Variable>
)

class DecisionMakingProblem(
    private val knowledge: Knowledge,
    private val outputPropertyBuilder: FactsBuilder.() -> Unit
) {
    private val outputProperty by lazy {
        FactsBuilder(knowledge.variables)
            .apply(outputPropertyBuilder)
            .facts.map { Fact(99, it) }
            .first()
    }

    fun solve(): Expr {
        val (su1, su2) = generateSequence(0, Int::inc)
            .take(2.pow(knowledge.variables.size))
            .map(::toEnv)
            .fold(Pair(emptySet(), emptySet()), ::solve)

        val s = su1 - su2
        return s.disj()
    }

    private fun solve(s: Pair<Set<Expr>, Set<Expr>>, env: Map<Int, Boolean>): Pair<Set<Expr>, Set<Expr>> {
        val fu = outputProperty.expr.eval(env)
        if (!fu) {
            return s
        }

        val solution = knowledge.facts.conj().expr.eval(env)
        val vars = knowledge.outputs.map { Expr.Value(env.getValue(it.identifier)) }.conj()

        if (!solution) {
            return Pair(s.first, s.second + vars)
        }

        return Pair(s.first + vars, s.second)
    }

    private fun toEnv(i: Int): Map<Int, Boolean> = i
        .bits()
        .zip(knowledge.variables)
        .map { Pair(it.second.identifier, it.first) }
        .toMap()
}

class AnalysisProblem(
    private val knowledge: Knowledge,
    private val inputPropertyBuilder: FactsBuilder.() -> Unit
) {
    private val inputProperty by lazy {
        FactsBuilder(knowledge.variables)
            .apply(inputPropertyBuilder)
            .facts.map { Fact(99, it) }
            .first()
    }

    fun solve(): Expr {
        return generateSequence(0, Int::inc)
            .take(2.pow(knowledge.variables.size))
            .map(::toEnv)
            .fold(emptySet(), ::solve)
            .disj()
    }

    private fun solve(s: Set<Expr>, env: Map<Int, Boolean>): Set<Expr> {
        val fu = inputProperty.expr.eval(env)
        if (!fu) {
            return s
        }

        val solution = knowledge.facts.conj().expr.eval(env)
        if (!solution) {
            return s
        }

        return s + knowledge.outputs.map { if (env.getValue(it.identifier)) it else Expr.Not(it) }.conj()
    }

    private fun toEnv(i: Int): Map<Int, Boolean> = i
        .bits()
        .zip(knowledge.variables)
        .map { Pair(it.second.identifier, it.first) }
        .toMap()

    fun decompose(): List<Layer> {
        val initialFacts = knowledge.facts + inputProperty

        val layer0 = Layer(
            emptyList(),
            knowledge.outputs
        )

        if (layer0.variables.isEmpty()) {
            println("Outputs is empty list")
            return emptyList()
        }

        val initialLayers = listOf(layer0)

        tailrec fun go(
            layers: List<Layer>,
            remainingVariables: List<Expr.Variable>,
            remainingFacts: List<Fact>
        ): List<Layer> {
            if (remainingFacts.isEmpty()) {
                return layers
            }

            val usedVariables = layers.flatMap(Layer::variables)
            val usedFacts = layers.flatMap(Layer::facts)
            val previousVariables = layers.last().variables

            val currentFacts = (remainingFacts.filter { it.expr.contains(previousVariables) } - usedFacts).sortedBy { it.identifier }
            val currentVariables = (currentFacts.flatMap { it.expr.variables() } - usedVariables).distinct().sortedBy { it.identifier }

            val layerN = Layer(currentFacts, currentVariables)

            return go(
                layers + listOf(layerN),
                remainingVariables - usedVariables - currentVariables,
                remainingFacts - usedFacts - currentFacts
            )
        }

        return go(initialLayers, knowledge.variables, initialFacts)
    }

    data class Layer(val facts: List<Fact>, val variables: List<Expr.Variable>)
}