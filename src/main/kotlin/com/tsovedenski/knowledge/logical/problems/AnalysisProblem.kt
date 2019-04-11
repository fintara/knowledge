package com.tsovedenski.knowledge.logical.problems

import com.tsovedenski.knowledge.logical.*

/**
 * Created by Tsvetan Ovedenski on 11/04/19.
 */
class AnalysisProblem(
    private val knowledge: Knowledge,
    private val inputPropertyBuilder: FactsBuilder.() -> Expr
) {
    val inputProperty by lazy {
        val expr = FactsBuilder(knowledge.variables).inputPropertyBuilder()
        Fact(99, expr)
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

        return s + knowledge.outputs.map { if (env.getValue(it.identifier)) it else Expr.Not(
            it
        )
        }.conj()
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

            val layerN =
                Layer(currentFacts, currentVariables)

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