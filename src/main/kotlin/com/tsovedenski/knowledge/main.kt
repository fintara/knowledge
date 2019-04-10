package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-09.
 */
fun main() {
    val slfs = listOf(
        SLF(1, "speed is over 180 kmh"),
        SLF(2, "there is danger"),
        SLF(3, "speed is over 130 kmh"),
        SLF(4, "driver gets a ticket"),
        SLF(5, "engine is 75 hp"),
        SLF(6, "there are 6 people in car")
    )

    val knowledge = listOf(
        Fact(slfs) { slf(1) implies slf(2) },
        Fact(slfs) { slf(3) implies slf(4) },
        Fact(slfs) { slf(1) implies slf(3) },
        Fact(slfs) { slf(5) implies !slf(1) },
        Fact(slfs) { slf(6) implies !slf(1) }
    )
}

data class SLF(val identifier: Int, val label: String)

data class Fact(val slfs: List<SLF>, val expressionBuilder: ExpressionBuilder.() -> Operator)

sealed class Operator {
    data class And(val left: SLF, val right: SLF) : Operator()
    data class Or(val left: SLF, val right: SLF) : Operator()
    data class Implies(val left: SLF, val right: SLF) : Operator()
    data class Not(val value: SLF) : Operator()
}

fun Operator.eval(): SLF = TODO()

infix fun SLF.and(other: SLF) = Operator.And(this, other)
infix fun SLF.and(other: Operator) = Operator.And(this, other.eval())
infix fun SLF.or(other: SLF) = Operator.Or(this, other)
infix fun SLF.implies(other: SLF) = Operator.Implies(this, other)
infix fun SLF.implies(other: Operator) = Operator.Implies(this, other.eval())
operator fun SLF.not() = Operator.Not(this)

class ExpressionBuilder (private val slfs: List<SLF>) {
    fun slf(identifier: Int): SLF = slfs.find { it.identifier == identifier }!!
}