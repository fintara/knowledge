package com.tsovedenski.knowledge.logical

import com.tsovedenski.knowledge.logical.problems.DecisionMakingProblem
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 11/04/19.
 */
private fun TestBody.runSolution(
    knowledge: Knowledge,
    propertyBuilder: FactsBuilder.() -> Expr,
    expectedBuilder: ExprBuilder.() -> Expr
) {
    val dmp = DecisionMakingProblem(knowledge, propertyBuilder)
    val actual = dmp.solve()
    val expected = ExprBuilder(knowledge).expectedBuilder()

    assertEquals(expected, actual)
}

object DecisionMakingProblemSpec : Spek({

    describe("solution") {
        it ("solves cake knowledge") {
            runSolution(
                cakeKnowledge,
                { ref(3) },
                { ref(1) and ref(2) }
            )
        }
    }

})