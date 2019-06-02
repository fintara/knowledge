package com.tsovedenski.knowledge.associationrulesext

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
fun <A, B> List<Pair<A, B>>.invert(): Pair<List<A>, List<B>> {
    val firsts = mutableListOf<A>()
    val seconds = mutableListOf<B>()
    forEach { (a, b) ->
        firsts.add(a)
        seconds.add(b)
    }
    return Pair(firsts, seconds)
}