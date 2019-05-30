package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
class Knowledge (vararg val knowledge: List<Int>) {
    val size = knowledge[0].size
    val rows = knowledge.size

    fun discoverRules(support: Double): Set<Rule> {
        tailrec fun go(patterns: Set<Pattern>, solutions: List<Set<Pattern>> = emptyList()): Set<Pattern> {
            val joined = patterns.join()
            val checked = joined
                .filter { pattern -> pattern.subpatterns().all { it in patterns } }
            val next = checked.filter { supportFor(it) >= support }.toSet()

            if (next.isEmpty()) {
                return solutions.flatten().toSet()
            }

            return go(next, listOf(next) + solutions)
        }

        val properties = go(getSimplePatterns().filter { supportFor(it) >= support }.toSet())

        val rules = mutableListOf<Rule>()
        var pivot = 0

        while (pivot < size) {
            pivot++

            properties
                .map { Rule(
                    it.subList(0, pivot).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index, value.value) else null }.filterNotNull().toSet(),
                    it.subList(pivot, size).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index + pivot, value.value) else null }.filterNotNull().toSet()
                ) }
                .also { rules.addAll(it) }
        }

        properties.forEach(::println)
        println()

        val clean = rules.filter { it.head.isNotEmpty() && it.tail.isNotEmpty() }

        return (clean + clean.map(Rule::invert)).toSet()
    }

    fun getSimplePatterns(): Set<Pattern> {
        val empty = List(size) { Value.Any }
        return (0 until size).flatMap { attr ->
            knowledge.getValues(attr).map { empty.fix(attr, it) }
        }.toSet()
    }

    fun supportFor(row: Pattern): Double {
        val den = knowledge.map { pairwise(it, row) }.sum().toDouble()
        return den / rows
    }

    fun supportFor(rule: Rule): Double {
        val num = supportFor(rule.toPattern(size))
        return num / rows
    }

    private fun pairwise(data: List<Int>, row: Pattern): Int {
        val sum = data.zip(row).map { (a, b) ->
            when (b) {
                is Value.Any -> 0
                is Value.Fixed -> if (a == b.value) 1 else 0
            }
        }.sum()
        return if (sum == row.degree) 1 else 0
    }

    private fun Array<out List<Int>>.getValues(attribute: Int) = map { it[attribute] } . distinct() . sorted()
}