package com.tsovedenski.knowledge.logical

/**
 * Created by Tsvetan Ovedenski on 11/04/19.
 */
internal fun Knowledge.factRef(id: Int) = facts.find { it.identifier == id }
internal fun Knowledge.varRef(id: Int) = variables.find { it.identifier == id }!!

internal class ExprBuilder (val knowledge: Knowledge) {
    fun ref(id: Int) = knowledge.varRef(id)
}