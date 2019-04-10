package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-09.
 */
fun main() {
    val ap = AnalysisProblem(plantKnowledge) { create { ref(1) and ref(6) } }

    val decomposed = ap.decompose()

    decomposed.forEachIndexed { index, layer ->
        if (layer.facts.isNotEmpty()) {
            println("F_$index=(${layer.facts.map { it.identifier }.joinToString(",")})")
        }
        println("alpha_$index=(${layer.variables.map { it.identifier }.joinToString(",")})")
    }
}

data class Fact(val identifier: Int, val expr: Expr)

data class Knowledge(
    val facts: List<Fact>,
    val variables: List<Expr.Variable>,
    val inputProperties: List<Int>,
    val outputProperties: List<Int>
)

class AnalysisProblem(
    private val knowledge: Knowledge,
    private val inputProperty: FactsBuilder.() -> Unit
) {
    fun decompose(): List<Layer> {
        val initialFacts = knowledge.facts + FactsBuilder(knowledge.variables).apply(inputProperty).facts.map { Fact(99, it) }

        val layer0 = Layer(
            emptyList(),
            knowledge.outputProperties.map { id -> knowledge.variables.find { it.identifier == id }!! }
        )

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