package com.tsovedenski.knowledge.logical

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
    protected val facts: MutableList<Expr> = mutableListOf()

    fun ref(identifier: Int) = variables.find { it.identifier == identifier }!!
    fun fact(action: () -> Expr) = facts.add(action())

    infix fun Expr.implies(tail: Int) = Expr.Implies(this, ref(tail))
    infix fun Int.implies(tail: Int) = Expr.Implies(ref(this), ref(tail))
    infix fun Int.implies(tail: Expr) = Expr.Implies(ref(this), tail)
    infix fun Expr.and(right: Int) = Expr.And(this, ref(right))
    infix fun Int.and(right: Expr) = Expr.And(ref(this), right)
    infix fun Expr.or(right: Int) = Expr.Or(this, ref(right))
    infix fun Int.or(right: Expr) = Expr.Or(ref(this), right)

    operator fun Int.not() = Expr.Not(ref(this))

    fun build() = facts.toList()
}

@KnowledgeDsl
class VariableBuilder {
    protected val variables: MutableList<Expr.Variable> = mutableListOf()
    protected val inputs: MutableList<Int> = mutableListOf()
    protected val outputs: MutableList<Int> = mutableListOf()

    fun create(identifier: Int, label: String = "Variable $identifier") = variables.add(
        Expr.Variable(
            identifier,
            label
        )
    )
    fun inputs(vararg values: Int) = inputs.addAll(values.toList())
    fun outputs(vararg values: Int) = outputs.addAll(values.toList())

    fun build() = Triple(variables.toList(), inputs.toList(), outputs.toList())
}


@DslMarker
annotation class KnowledgeDsl

fun knowledge(action: KnowledgeBuilder.() -> Unit): Knowledge {
    val builder = KnowledgeBuilder()
    builder.action()
    val (variables, inputs, outputs) = builder.variablesBuilder.build()
    return Knowledge(
        builder.factsBuilder.build().mapIndexed { i, expr -> Fact(i + 1, expr) },
        variables,
        inputs.map { id -> variables.find { it.identifier == id }!! },
        outputs.map { id -> variables.find { it.identifier == id }!! }
    )
}

fun KnowledgeBuilder.facts(action: FactsBuilder.() -> Unit) {
    factsBuilder = FactsBuilder(variablesBuilder.build().first)
    factsBuilder.action()
}

fun KnowledgeBuilder.variables(action: VariableBuilder.() -> Unit) {
    variablesBuilder = VariableBuilder()
    variablesBuilder.action()
}