package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
data class Rule (val head: Set<Attribute>, val tail: Set<Attribute>) {
    constructor(head: Attribute, tail: Set<Attribute>) : this(setOf(head), tail)
    constructor(head: Set<Attribute>, tail: Attribute) : this(head, setOf(tail))
    constructor(head: Attribute, tail: Attribute) : this(setOf(head), setOf(tail))

    private val size = head.size + tail.size

    fun invert(): Rule = Rule(tail, head)

    fun permutations(): Set<Rule> {
        tailrec fun go(rule: Rule, rs: Set<Rule> = emptySet()): Set<Rule> {
            if (rs.size == size) {
                return rs
            }

            val h = rule.head.first()
            val t = rule.tail.first()
            val next = Rule(rule.head.drop(1).toSet() + t, rule.tail.drop(1).toSet() + h)

            return go(next, rs + next)
        }

        val first = go(this, setOf(this))
        return (first + first.map(Rule::invert)).toSet()
    }

    fun toPattern(size: Int): Pattern {
        val pattern: MutableList<Value> = MutableList(size) { Value.Any }
        head.forEach { pattern[it.attribute] = Value.Fixed(it.relation, it.value) }
        tail.forEach { pattern[it.attribute] = Value.Fixed(it.relation, it.value) }
        return pattern
    }

    override fun toString(): String {
        val p = head.joinToString(" ∧ ")
        val implies = "⇒"
        val q = tail.joinToString(" ∧ ")

        return "$p $implies $q"
    }
}