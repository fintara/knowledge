package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-28.
 */
data class Knowledge (val knowledge: List<List<Int>>) {
    val size = knowledge[0].size
    val rows = knowledge.size

    fun getSimplePatterns(): List<List<Value>> {
        val empty = List(size) { Value.Any }
        return (0 until size).flatMap { attr ->
            knowledge.getValues(attr).map { empty.fix(attr, it) }
        }
    }

    fun supportFor(row: List<Value>): Double {
        val den = knowledge.map { pairwise(it, row) }.sum().toDouble()
        return den / rows
    }

    private fun pairwise(data: List<Int>, row: List<Value>): Int {
        return data.zip(row).map { (a, b) ->
            when (b) {
                is Value.Any -> 0
                is Value.Fixed -> if (a == b.value) 1 else 0
            }
        }.sum()
    }

    private fun List<Value>.fix(attribute: Int, value: Int): List<Value> {
        val copy = toMutableList()
        copy[attribute] = Value.Fixed(value)
        return copy
    }
}

fun main() {
    val knowledge = Knowledge(listOf(
        listOf(1,2,1),
        listOf(2,0,0),
        listOf(2,3,0),
        listOf(2,1,1),
        listOf(2,2,0),
        listOf(3,3,1),
        listOf(2,1,1)
    ))

    val patterns = knowledge.getSimplePatterns()
    patterns.forEach {
        println("$it (${knowledge.supportFor(it)})")
    }
}

sealed class Value {
    object Any : Value() {
        override fun toString(): String {
            return "_"
        }
    }
    data class Fixed(val value: Int) : Value() {
        override fun toString(): String {
            return "$value"
        }
    }
}

fun List<List<Int>>.getValues(attribute: Int) = map { it[attribute] } . distinct() . sorted()