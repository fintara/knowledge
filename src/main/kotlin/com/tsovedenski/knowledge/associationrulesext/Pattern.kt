package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
typealias Pattern = List<Value>

fun pattern(vararg values: Int?): Pattern = values.map { when (it) {
    is Int -> Value.Fixed(Relation.EQ, it)
    else   -> Value.Any
} }

fun Pattern.fix(attribute: Int, relation: Relation, value: Int): Pattern {
    val copy = toMutableList()
    copy[attribute] = Value.Fixed(relation, value)
    return copy
}

fun Pattern.unfix(attribute: Int): Pattern {
    val copy = toMutableList()
    copy[attribute] = Value.Any
    return copy
}

fun Set<Pattern>.join(): Set<Pattern> {
    val pairs = zip(0 until size-1)
        .flatMap { (row, i) ->
            (i+1 until size).map { j -> Pair(row, elementAt(j)) }
        }

    fun join(first: Pattern, second: Pattern): Pattern? {
        return first.zip(second).map { (a, b) -> when {
            a is Value.Fixed && b is Value.Fixed -> if (a.value == b.value && a.relation == b.relation) a else return null
            a is Value.Fixed -> a
            b is Value.Fixed -> b
            else -> Value.Any
        } }
    }

    return pairs.mapNotNull { (a, b) -> join(a, b) }.toSet()
}

fun Iterable<Pattern>.simplify(): Set<Pattern> {
//    val pairs = zip(0 until count()-1)
//        .flatMap { (row, i) ->
//            (i+1 until count()).map { j -> Pair(row, elementAt(j)) }
//        }
    val pairs = zipWithNext()

    fun simplify(first: Pattern, second: Pattern): Set<Pattern> {
        val each: List<Set<Value>> = first.zip(second).map { (a, b) -> a.simplify(b) }
        return if (each.all { it.size == 1 }) setOf(each.flatten()) else setOf(first, second)
    }

    return pairs.flatMap { (a, b) -> simplify(a, b) }.toSet()
}

fun Pattern.split(): Set<Pair<Pattern, Pattern>> {
    val currentDegree = degree
    if (currentDegree < 2) {
        return emptySet()
    }

    val splits = mutableListOf<Pair<Pattern, Pattern>>()
    val pattern = MutableList(size) { false }
    var pivot = 0

    while (pivot < pattern.size) {
        pattern[pivot] = true

        val mapped = zip(pattern).mapIndexed { idx, (value, bit) ->
            Pair(
                if (bit) value else Value.Any,
                if (bit) Value.Any else value
            )
        }

        splits.add(mapped.invert())

        pivot++
    }

    return splits
        .filter { with(it.first.degree) { this == it.second.degree && this == currentDegree - 1 } }
        .toSet()
}

fun Pattern.subpatterns(): Set<Pattern> {
    val currentDegree = degree
    if (currentDegree < 2) {
        return emptySet()
    }

    return (0 until size).map { unfix(it) }.filter { it.degree == currentDegree - 1 }.toSet()
}