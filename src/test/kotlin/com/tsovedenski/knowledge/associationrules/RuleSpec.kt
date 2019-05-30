package com.tsovedenski.knowledge.associationrules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
object RuleSpec : Spek({
    val rule2 = Rule(Equal(0, 2), Equal(2, 1))
    val rule3 = Rule(Equal(0, 2), setOf(Equal(1, 1), Equal(2, 1)))

    describe("invert") {
        it("inverts head and tail") {
            val next = rule3.invert()

            assertEquals(rule3.tail, next.head)
            assertEquals(rule3.head, next.tail)
        }
    }

    describe("permutations") {
        it("returns all variations for size = 2") {
            val permutations = rule2.permutations()

            assertEquals(2, permutations.size)
        }

        it("returns all variations for size = 3") {
            val permutations = rule3.permutations()

            assertEquals(6, permutations.size)
        }
    }

    describe("toPattern") {
        it("converts to pattern deg = 3 when size = 2") {
            val pattern = rule2.toPattern(3)

            assertEquals(rule2.head.elementAt(0).value, pattern[0].unwrap())
            assertNull(pattern[1].unwrap())
            assertEquals(rule2.tail.elementAt(0).value, pattern[2].unwrap())
        }
        it("converts to pattern deg = 3 when size = 3") {
            val pattern = rule3.toPattern(3)

            assertEquals(rule3.head.elementAt(0).value, pattern[0].unwrap())
            assertEquals(rule3.tail.elementAt(0).value, pattern[1].unwrap())
            assertEquals(rule3.tail.elementAt(1).value, pattern[2].unwrap())
        }
        it("converts x(0)=2 => x(2)=0") {
            val rule = Rule(Equal(0,2), Equal(2, 0))
            val pattern = rule.toPattern(3)

            assertEquals(2, pattern[0].unwrap())
            assertNull(pattern[1].unwrap())
            assertEquals(0, pattern[2].unwrap())
        }
    }
})