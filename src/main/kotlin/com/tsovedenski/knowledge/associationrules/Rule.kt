package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
data class Rule (val head: Set<Equal>, val tail: Set<Equal>) {
    constructor(head: Equal, tail: Set<Equal>) : this(setOf(head), tail)
    constructor(head: Set<Equal>, tail: Equal) : this(head, setOf(tail))
    constructor(head: Equal, tail: Equal) : this(setOf(head), setOf(tail))

    fun invert(): Rule = Rule(tail, head)

    fun toPattern(size: Int): Pattern {
        val pattern: MutableList<Value> = MutableList(size) { Value.Any }
        head.forEach { pattern[it.attribute] = Value.Fixed(it.value) }
        tail.forEach { pattern[it.attribute] = Value.Fixed(it.value) }
        return pattern
    }

    override fun toString(): String {
        val p = head.joinToString(" ∧ ")
        val implies = "⇒"
        val q = tail.joinToString(" ∧ ")

        return "$p $implies $q"
    }
}