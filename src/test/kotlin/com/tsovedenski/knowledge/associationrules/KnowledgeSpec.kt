package com.tsovedenski.knowledge.associationrules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNull
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
object KnowledgeSpec : Spek({
    val knowledge = Knowledge(
        listOf(1,2,1),
        listOf(2,0,0),
        listOf(2,3,0),
        listOf(2,1,1),
        listOf(2,2,0),
        listOf(3,3,1),
        listOf(2,1,1)
    )

    describe("getSimplePatterns") {
        val simplePatterns by memoized { knowledge.getSimplePatterns() }
        it("returns 9 simple patterns") {
            assertEquals(9, simplePatterns.size)
        }

        val xs = (1..3).map { pattern(it, null, null) }
        val ys = (0..3).map { pattern(null, it, null) }
        val zs = (0..1).map { pattern(null, null, it) }

        (xs + ys + zs).forEach { pattern ->
            it("$pattern is in simple patterns") {
                assertTrue(pattern in simplePatterns)
            }
        }
    }

    describe("supportFor pattern") {
         mapOf(
             pattern(2, null, null) to 5/7.0,
             pattern(2, null, 1)    to 2/7.0,
             pattern(2, null, 0)    to 3/7.0,
             pattern(null, null, 0) to 3/7.0,
             pattern(null, null, 1) to 4/7.0,
             pattern(2, 1, 1)       to 2/7.0,
             pattern(1, 2, 1)       to 1/7.0,
             pattern(null, 1, 1)    to 2/7.0,
             pattern(3, 3, null)    to 1/7.0
        ).forEach { pattern, support ->
             it("$pattern has support ${"%.2f".format(support)}") {
                 assertEquals(support, knowledge.supportFor(pattern))
             }
         }
    }

    describe("supportFor rule") {
        mapOf(
            Rule(Equal(0, 2), Equal(2, 0)) to 3/7.0,
            Rule(Equal(0, 2), setOf(Equal(1, 3), Equal(2, 0))) to 1/7.0
        ).forEach { rule, support ->
            it("'$rule' has support ${"%.2f".format(support)}") {
                assertEquals(support, knowledge.supportFor(rule))
            }
        }
    }

    describe("confidenceFor rule") {
        mapOf(
            Rule(0, 2, 1, 1) to 2/5.0,
            Rule(1, 1, 0, 2) to 1.0,
            Rule(0, 2, 2, 0) to 3/5.0,
            Rule(2, 0, 0, 2) to 1.0,
            Rule(0, 2, 2, 1) to 2/5.0,
            Rule(2, 1, 0, 2) to 0.5,
            Rule(1, 1, 2, 1) to 1.0,
            Rule(2, 1, 1, 1) to 0.5,
            Rule(Equal(0, 2), setOf(Equal(1, 1), Equal(2, 1))) to 2/5.0,
            Rule(setOf(Equal(1, 1), Equal(2, 1)), Equal(0, 2)) to 1.0,
            Rule(Equal(2, 1), setOf(Equal(0, 2), Equal(1, 1))) to 0.5,
            Rule(setOf(Equal(0, 2), Equal(1, 1)), Equal(2, 1)) to 1.0,
            Rule(Equal(1, 1), setOf(Equal(0, 2), Equal(2, 1))) to 1.0,
            Rule(setOf(Equal(0, 2), Equal(2, 1)), Equal(1, 1)) to 1.0
        ).forEach { (rule, confidence) ->
            it("'$rule' has confidence ${"%.2f".format(confidence)}") {
                assertEquals(confidence, knowledge.confidenceFor(rule))
            }
        }
    }

    describe("discoverProperties") {
        val properties by memoized { knowledge.discoverProperties(2/7.0) }
        it("returns 5 properties") {
            assertEquals(5, properties.size)
        }

        setOf(
            pattern(2, 1, 1),
            pattern(2, 1, null),
            pattern(2, null, 0),
            pattern(2, null, 1),
            pattern(null, 1, 1)
        ).forEach { pattern ->
            it("contains $pattern") {
                assertTrue(pattern in properties)
            }
        }
    }

    describe("discoverRules with confidence = 0") {
        val rules by memoized { knowledge.discoverRules(2/7.0, 0.0) }
        it("finds 14 rules for s=2/7") {
            assertEquals(14, rules.size)
        }

        setOf(
            Rule(0, 2, 1, 1),
            Rule(1, 1, 0, 2),
            Rule(0, 2, 2, 0),
            Rule(2, 0, 0, 2),
            Rule(0, 2, 2, 1),
            Rule(2, 1, 0, 2),
            Rule(1, 1, 2, 1),
            Rule(2, 1, 1, 1),
            Rule(Equal(0, 2), setOf(Equal(1, 1), Equal(2, 1))),
            Rule(setOf(Equal(1, 1), Equal(2, 1)), Equal(0, 2)),
            Rule(Equal(2, 1), setOf(Equal(0, 2), Equal(1, 1))),
            Rule(setOf(Equal(0, 2), Equal(1, 1)), Equal(2, 1)),
            Rule(Equal(1, 1), setOf(Equal(0, 2), Equal(2, 1))),
            Rule(setOf(Equal(0, 2), Equal(2, 1)), Equal(1, 1))
        ).forEach { rule ->
            it("contains $rule") {
                assertTrue(rule in rules)
            }
        }
    }

    describe("discoverRules with confidence = 1") {
        val rules by memoized { knowledge.discoverRules(2/7.0, 1.0) }
        it("finds 7 rules") {
            assertEquals(7, rules.size)
        }

        setOf(
            Rule(1, 1, 0, 2),
            Rule(2, 0, 0, 2),
            Rule(1, 1, 2, 1),
            Rule(setOf(Equal(1, 1), Equal(2, 1)), Equal(0, 2)),
            Rule(setOf(Equal(0, 2), Equal(1, 1)), Equal(2, 1)),
            Rule(Equal(1, 1), setOf(Equal(0, 2), Equal(2, 1))),
            Rule(setOf(Equal(0, 2), Equal(2, 1)), Equal(1, 1))
        ).forEach { rule ->
            it("contains $rule") {
                assertTrue(rule in rules)
            }
        }
    }
})