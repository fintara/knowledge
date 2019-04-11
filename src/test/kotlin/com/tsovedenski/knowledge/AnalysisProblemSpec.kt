package com.tsovedenski.knowledge

import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 11/04/19.
 */
private fun TestBody.runDecomposition(
    knowledge: Knowledge,
    propertyBuilder: FactsBuilder.() -> Unit,
    expected: List<Pair<List<Int>, List<Int>>>
) {
    val ap = AnalysisProblem(knowledge, propertyBuilder)
    val actual = ap.decompose()
    val expectedLayers = expected.map { layer ->
        AnalysisProblem.Layer(
            layer.first.map { knowledge.factRef(it) ?: ap.inputProperty },
            layer.second.map { knowledge.varRef(it) }
        )
    }

    assertEquals(expectedLayers, actual)
}

private fun TestBody.runSolution(
    knowledge: Knowledge,
    propertyBuilder: FactsBuilder.() -> Unit,
    expectedBuilder: ExprBuilder.() -> Expr
) {
    val ap = AnalysisProblem(knowledge, propertyBuilder)
    val actual = ap.solve()
    val expected = ExprBuilder(knowledge).expectedBuilder()

    assertEquals(expected, actual)
}

object AnalysisProblemSpec : Spek({

    describe("decomposition") {
        it("decomposes driver knowledge") {
            runDecomposition(
                driverKnowledge,
                { create { !ref(1) and ref(3) } },
                listOf(
                    Pair(
                        emptyList(),
                        listOf(2, 4)
                    ),
                    Pair(
                        listOf(1, 2),
                        listOf(1, 3)
                    ),
                    Pair(
                        listOf(3, 4, 5, 99),
                        listOf(5, 6)
                    )
                )
            )
        }

        it("decomposes random knowledge") {
            runDecomposition(
                randomKnowledge,
                { create { !ref(1) } },
                listOf(
                    Pair(
                        emptyList(),
                        listOf(9, 10)
                    ),
                    Pair(
                        listOf(1, 2),
                        listOf(3, 4)
                    ),
                    Pair(
                        listOf(3, 4),
                        listOf(5, 6)
                    ),
                    Pair(
                        listOf(5, 6),
                        listOf(7, 8)
                    ),
                    Pair(
                        listOf(7),
                        listOf(1, 2)
                    ),
                    Pair(
                        listOf(99),
                        emptyList()
                    )
                )
            )
        }
    }

    describe("solution") {
        it("solves driver knowledge") {
            runSolution(
                driverKnowledge,
                { create { !ref(1) and ref(3) } },
                { (!ref(2) and ref(4)) or (ref(2) and ref(4)) }
            )
        }

        it("solves random knowledge") {
            runSolution(
                randomKnowledge,
                { create { !ref(1) } },
                { ref(9) and ref(10) }
            )
        }
    }

})