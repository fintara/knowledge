package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
data class Equal(val attribute: Int, val value: Int) {
    override fun toString(): String {
        return "x($attribute)=$value"
    }
}

fun Iterable<Equal>.toPattern(size: Int): Pattern {
    val pattern: MutableList<Value> = MutableList(size) { Value.Any }
    forEach { pattern[it.attribute] = Value.Fixed(it.value) }
    return pattern
}