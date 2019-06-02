package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
sealed class Value {
    abstract fun unwrap(): Int?

    object Any : Value() {
        override fun toString(): String {
            return "_"
        }

        override fun unwrap(): Int? {
            return null
        }
    }
    data class Fixed(val relation: Relation, val value: Int) : Value() {
        override fun toString(): String {
            return "$relation$value"
        }

        override fun unwrap(): Int? {
            return value
        }
    }
}

val Iterable<Value>.degree: Int get() = filterIsInstance<Value.Fixed>().size

fun Value.simplify(other: Value): Set<Value> = when {
    this == other -> setOf(this)

    this is Value.Fixed && other is Value.Fixed -> when {
        value == other.value+1 && relation == Relation.LEQ && other.relation == Relation.GEQ -> setOf(Value.Any)
        value == other.value-1 && relation == Relation.GEQ && other.relation == Relation.LEQ -> setOf(Value.Any)
        else -> setOf(this, other)
    }

    else -> setOf(this, other)
}

