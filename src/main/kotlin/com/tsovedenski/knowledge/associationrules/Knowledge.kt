package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
class Knowledge (vararg val knowledge: List<Int>) {
    private val size = knowledge[0].size
    private val rows = knowledge.size

    fun discoverProperties(support: Double): Set<Pattern> {
        tailrec fun go(patterns: Set<Pattern>, solutions: List<Set<Pattern>> = emptyList()): Set<Pattern> {
            val joined = patterns.join()
            val checked = joined.filter { pattern -> pattern.subpatterns().all { it in patterns } }
            val next = checked.filter { supportFor(it) >= support }.toSet()

            if (next.isEmpty()) {
                return solutions.flatten().toSet()
            }

            return go(next, listOf(next) + solutions)
        }

        return go(getSimplePatterns().filter { supportFor(it) >= support }.toSet())
    }

    fun discoverRules(support: Double, confidence: Double): Set<Rule> {
        val properties = discoverProperties(support)

        val rules = (0 until size).flatMap { pivot ->
            properties
                .map { Rule(
                    it.subList(0, pivot).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index, value.value) else null }.filterNotNull().toSet(),
                    it.subList(pivot, size).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index + pivot, value.value) else null }.filterNotNull().toSet()
                ) }
        }

        val clean = rules.filter { it.head.isNotEmpty() && it.tail.isNotEmpty() }

        return clean.flatMap { it.permutations() }.filter { confidenceFor(it) >= confidence }.toSet()
    }

    fun getSimplePatterns(): Set<Pattern> {
        val empty = List(size) { Value.Any }
        return (0 until size).flatMap { attr ->
            knowledge.getValues(attr).map { empty.fix(attr, it) }
        }.toSet()
    }

    fun supportFor(row: Pattern): Double {
        val num = knowledge.countPattern(row)
        return num / rows
    }

    fun supportFor(rule: Rule): Double {
        return supportFor(rule.toPattern(size))
    }

    fun confidenceFor(rule: Rule): Double {
        val rulePattern = rule.toPattern(size)
        val num = knowledge.countPattern(rulePattern)

        val headPattern = rule.head.toPattern(size)
        val den = knowledge.countPattern(headPattern)

        return num / den
    }

    fun liftFor(rule: Rule): Double {
        val num = supportFor(rule)
        val den = supportFor(rule.head.toPattern(size)) * supportFor(rule.tail.toPattern(size))

        return num / den
    }

    private fun checkRowPattern(row: List<Int>, pattern: Pattern): Int {
        val sum = row.zip(pattern).map { (a, b) ->
            when (b) {
                is Value.Any -> 0
                is Value.Fixed -> if (a == b.value) 1 else 0
            }
        }.sum()
        return if (sum == pattern.degree) 1 else 0
    }

    private fun Array<out List<Int>>.getValues(attribute: Int)
            = map { it[attribute] } . distinct() . sorted()

    private fun Array<out List<Int>>.countPattern(pattern: Pattern)
            = map { checkRowPattern(it, pattern) } . sum() . toDouble()
}