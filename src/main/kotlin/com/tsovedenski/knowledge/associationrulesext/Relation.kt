package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 02/06/19.
 */
enum class Relation (private val ch: Char) {
    NEQ('≠') {
        override val opposite get() = EQ
        override fun compare(a: Int, b: Int): Boolean {
            return a != b
        }
    },

    EQ('=') {
        override val opposite get() = NEQ
        override fun compare(a: Int, b: Int): Boolean {
            return a == b
        }
    },

    LT('<') {
        override val opposite get() = GEQ
        override fun compare(a: Int, b: Int): Boolean {
            return a < b
        }
    },

    LEQ('≤') {
        override val opposite get() = GT
        override fun compare(a: Int, b: Int): Boolean {
            return a <= b
        }
    },

    GT('>') {
        override val opposite get() = LEQ
        override fun compare(a: Int, b: Int): Boolean {
            return a > b
        }
    },

    GEQ('≥') {
        override val opposite get() = LT
        override fun compare(a: Int, b: Int): Boolean {
            return a >= b
        }
    };

    abstract val opposite: Relation
    abstract fun compare(a: Int, b: Int): Boolean

    override fun toString(): String {
        return "$ch"
    }
}