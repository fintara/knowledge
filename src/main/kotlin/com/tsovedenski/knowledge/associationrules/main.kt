package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-28.
 */
typealias Pattern = List<Value>
fun pattern(vararg values: Any): Pattern = values.map { when (it) {
    is Int -> Value.Fixed(it)
    else   -> Value.Any
} }

class Knowledge (vararg val knowledge: List<Int>) {
    val size = knowledge[0].size
    val rows = knowledge.size

    fun discoverRules(support: Double): Set<Rule> {
        tailrec fun go(patterns: Set<Pattern>, solutions: List<Set<Pattern>> = emptyList()): Set<Pattern> {
            val joined = patterns.join()
            val checked = joined
                .filter { pattern -> pattern.subpatterns().all { it in patterns } }
            val next = checked.filter { supportFor(it) >= support }.toSet()

            if (next.isEmpty()) {
                return solutions.flatten().toSet()
            }

            return go(next, listOf(next) + solutions)
        }

        val properties = go(getSimplePatterns().filter { supportFor(it) >= support }.toSet())

        val rules = mutableListOf<Rule>()
        var pivot = 0

        while (pivot < size) {
            pivot++

            properties
                .map { Rule(
                    it.subList(0, pivot).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index, value.value) else null }.filterNotNull().toSet(),
                    it.subList(pivot, size).mapIndexed { index, value -> if (value is Value.Fixed) Equal(index + pivot, value.value) else null }.filterNotNull().toSet()
                ) }
                .also { rules.addAll(it) }
        }

        properties.forEach(::println)
        println()

        val clean = rules.filter { it.head.isNotEmpty() && it.tail.isNotEmpty() }

        return (clean + clean.map(Rule::invert)).toSet()
    }

    fun getSimplePatterns(): Set<Pattern> {
        val empty = List(size) { Value.Any }
        return (0 until size).flatMap { attr ->
            knowledge.getValues(attr).map { empty.fix(attr, it) }
        }.toSet()
    }

    fun supportFor(row: Pattern): Double {
        val den = knowledge.map { pairwise(it, row) }.sum().toDouble()
        return den / rows
    }

    private fun pairwise(data: List<Int>, row: Pattern): Int {
        val sum = data.zip(row).map { (a, b) ->
            when (b) {
                is Value.Any -> 0
                is Value.Fixed -> if (a == b.value) 1 else 0
            }
        }.sum()
        return if (sum == row.degree) 1 else 0
    }

    private fun Pattern.fix(attribute: Int, value: Int): Pattern {
        val copy = toMutableList()
        copy[attribute] = Value.Fixed(value)
        return copy
    }

    private fun Pattern.unfix(attribute: Int): Pattern {
        val copy = toMutableList()
        copy[attribute] = Value.Any
        return copy
    }

    private fun Array<out List<Int>>.getValues(attribute: Int) = map { it[attribute] } . distinct() . sorted()

    private fun Set<Pattern>.join(): Set<Pattern> {
        val pairs = zip(0 until size-1)
            .flatMap { (row, i) ->
                (i+1 until size).map { j -> Pair(row, elementAt(j)) }
            }

        fun join(first: Pattern, second: Pattern): Pattern? {
            return first.zip(second).map { (a, b) -> when {
                a is Value.Fixed && b is Value.Fixed -> if (a.value == b.value) a else return null
                a is Value.Fixed -> a
                b is Value.Fixed -> b
                else -> Value.Any
            } }
        }

        return pairs.map { (a, b) -> join(a, b) }.filterNotNull().toSet()
    }

    private fun Pattern.split(): Set<Pair<Pattern, Pattern>> {
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
            .filter { it.first.degree == it.second.degree && it.first.degree == currentDegree - 1 }
            .toSet()
    }

    private fun Pattern.subpatterns(): Set<Pattern> {
        val currentDegree = degree
        if (currentDegree < 2) {
            return emptySet()
        }

        return (0 until size).map { unfix(it) }.filter { it.degree == currentDegree - 1 }.toSet()
    }

    private fun <A, B> List<Pair<A, B>>.invert(): Pair<List<A>, List<B>> {
        val firsts = mutableListOf<A>()
        val seconds = mutableListOf<B>()
        forEach { (a, b) ->
            firsts.add(a)
            seconds.add(b)
        }
        return Pair(firsts, seconds)
    }
}

fun main() {
    val knowledge = Knowledge(
        listOf(1,2,1),
        listOf(2,0,0),
        listOf(2,3,0),
        listOf(2,1,1),
        listOf(2,2,0),
        listOf(3,3,1),
        listOf(2,1,1)
    )

//    val patterns = knowledge.getSimplePatterns()
//    patterns.forEach {
//        println("$it (${knowledge.supportFor(it)})")
//    }

    val rules = knowledge.discoverRules(support = 2/7.0)
    rules.forEach { println(it) }
}

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

data class Equal(val attribute: Int, val value: Int) {
    override fun toString(): String {
        return "x($attribute)=$value"
    }
}

data class Rule (val head: Set<Equal>, val tail: Set<Equal>) {
    constructor(head: Equal, tail: Set<Equal>) : this(setOf(head), tail)
    constructor(head: Set<Equal>, tail: Equal) : this(head, setOf(tail))
    constructor(head: Equal, tail: Equal) : this(setOf(head), setOf(tail))

    fun invert(): Rule = Rule(tail, head)

    override fun toString(): String {
        val p = head.joinToString(" ∧ ")
        val implies = "⇒"
        val q = tail.joinToString(" ∧ ")

        return "$p $implies $q"
    }
}