package com.tsovedenski.knowledge.associationrules

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
    data class Fixed(val value: Int) : Value() {
        override fun toString(): String {
            return "$value"
        }

        override fun unwrap(): Int? {
            return value
        }
    }
}

val Iterable<Value>.degree: Int get() = filterIsInstance<Value.Fixed>().size