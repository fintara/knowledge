package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-10.
 */
@KnowledgeDsl
class KnowledgeBuilder {
    lateinit var factsBuilder: FactsBuilder
    lateinit var variablesBuilder: VariableBuilder
}

@KnowledgeDsl
class FactsBuilder(private val variables: List<Expr.Variable>) {
    val facts: MutableList<Expr> = mutableListOf()
    fun ref(identifier: Int) = variables.find { it.identifier == identifier }!!
    fun create(action: () -> Expr) = facts.add(action())
    infix fun Expr.implies(tail: Int) = Expr.Implies(this, ref(tail))
    infix fun Int.implies(tail: Int) = Expr.Implies(ref(this), ref(tail))
    infix fun Int.implies(tail: Expr) = Expr.Implies(ref(this), tail)
    infix fun Expr.and(right: Int) = Expr.And(this, ref(right))
    infix fun Int.and(right: Expr) = Expr.And(ref(this), right)
    infix fun Expr.or(right: Int) = Expr.Or(this, ref(right))
    infix fun Int.or(right: Expr) = Expr.Or(ref(this), right)
    operator fun Int.not() = Expr.Not(ref(this))
    operator fun invoke() = facts
}

@KnowledgeDsl
class VariableBuilder {
    val variables: MutableList<Expr.Variable> = mutableListOf()
    val inputs: MutableList<Int> = mutableListOf()
    val outputs: MutableList<Int> = mutableListOf()
    fun create(identifier: Int, label: String) = variables.add(Expr.Variable(identifier, label))
    fun inputs(vararg values: Int) = inputs.addAll(values.toList())
    fun outputs(vararg values: Int) = outputs.addAll(values.toList())
}


@DslMarker
annotation class KnowledgeDsl

fun knowledge(action: KnowledgeBuilder.() -> Unit): Knowledge {
    val builder = KnowledgeBuilder()
    builder.action()
    return Knowledge(
        builder.factsBuilder.facts.mapIndexed { i, expr -> Fact(i+1, expr) },
        builder.variablesBuilder.variables,
        builder.variablesBuilder.inputs,
        builder.variablesBuilder.outputs
    )
}

fun KnowledgeBuilder.facts(action: FactsBuilder.() -> Unit) {
    factsBuilder = FactsBuilder(variablesBuilder.variables)
    factsBuilder.action()
}

fun KnowledgeBuilder.variables(action: VariableBuilder.() -> Unit) {
    variablesBuilder = VariableBuilder()
    variablesBuilder.action()
}