package com.tsovedenski.knowledge.associationrules

/**
 * Created by Tsvetan Ovedenski on 2019-05-28.
 */
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

    val rules = knowledge.discoverRules(support = 2/7.0, confidence = 1.0).sortedBy(knowledge::liftFor)
    println("Found ${rules.size} rules")
    rules.forEach { println("$it (s = ${"%.2f".format(knowledge.supportFor(it))}, c = ${"%.2f".format(knowledge.confidenceFor(it))}, l = ${"%.2f".format(knowledge.liftFor(it))})") }

//    val rule = Rule(setOf(Equal(0, 2)), setOf(Equal(1, 1), Equal(2, 1)))

//    rule `|` ::println
//    rule.permutations().forEach(::println)
}

infix fun <T> T.`|`(action: (T) -> Unit): T = also(action)