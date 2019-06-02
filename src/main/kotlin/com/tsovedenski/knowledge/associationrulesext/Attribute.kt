package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 02/06/19.
 */
data class Attribute (val attribute: Int, val relation: Relation, val value: Int) {
    override fun toString(): String {
        return "x($attribute)$relation$value"
    }
}

fun Iterable<Attribute>.toPattern(size: Int): Pattern {
    val pattern: MutableList<Value> = MutableList(size) { Value.Any }
    forEach { pattern[it.attribute] = Value.Fixed(it.relation, it.value) }
    return pattern
}
