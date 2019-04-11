package com.tsovedenski.knowledge.logical.problems

import com.tsovedenski.knowledge.logical.*

/**
 * Created by Tsvetan Ovedenski on 11/04/19.
 */
class DecisionMakingProblem(
    private val knowledge: Knowledge,
    private val outputPropertyBuilder: FactsBuilder.() -> Expr
) {
    private val outputProperty by lazy {
        val expr = FactsBuilder(knowledge.variables).outputPropertyBuilder()
        Fact(99, expr)
    }

    fun solve(): Expr {
        val (su1, su2) = generateSequence(0, Int::inc)
            .take(2.pow(knowledge.variables.size))
            .map { toEnv(it, knowledge.variables.size) }
            .fold(Pair(emptySet(), emptySet()), ::solve)

        val s = su1 - su2
        return s.disj()
    }

    private fun solve(s: Pair<Set<Expr>, Set<Expr>>, env: Map<Int, Boolean>): Pair<Set<Expr>, Set<Expr>> {
        val fy = outputProperty.expr.eval(env)
        val f = knowledge.facts.conj().expr.eval(env)

        if (!f) {
            // doesn't agree with our knowledge
            return s
        }

        val vars = knowledge.inputs.map { if (env.getValue(it.identifier)) it else Expr.Not(
            it
        )
        }.conj()

        val pair = if (fy) {
            // possibly correct
            Pair(s.first + vars, s.second)
        } else {
            // possibly incorrect
            Pair(s.first, s.second + vars)
        }

        return pair
    }

    private fun toEnv(i: Int, length: Int): Map<Int, Boolean> = i
        .bits(length)
        .zip(knowledge.variables)
        .map { Pair(it.second.identifier, it.first) }
        .toMap()
}