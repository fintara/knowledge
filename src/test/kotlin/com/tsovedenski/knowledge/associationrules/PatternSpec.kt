package com.tsovedenski.knowledge.associationrules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
object PatternSpec : Spek({

    describe("join") {
        it("doesn't join without common part") {
            val pattern1 = pattern(2, null, 1)
            val pattern2 = pattern(1, 3, null)

            val joined = setOf(pattern1, pattern2).join()

            assertEquals(emptySet<Pattern>(), joined)
        }
        it("joins two patterns of deg 2") {
            val pattern1 = pattern(2, null, 1)
            val pattern2 = pattern(2, 3, null)

            val joined = setOf(pattern1, pattern2).join()
            val expected = setOf(pattern(2, 3, 1))

            assertEquals(expected, joined)
        }
    }

    describe("split") {
        it("splits deg 2") {
            val pattern = pattern(2, 1)
            val split = pattern.split()

            assertEquals(1, split.size)

            assertEquals(Pair(pattern(2, null), pattern(null, 1)), split.elementAt(0))
        }

//        it("splits deg 3") {
//            val pattern = pattern(2, 3, 1)
//            val split = pattern.split()
//
//            assertEquals(3, split.size)
//        }
    }

    describe("subpatterns") {
        it("returns 2 subpatterns for deg 2") {
            val pattern = pattern(2, 1)
            val subpatterns = pattern.subpatterns()

            assertEquals(2, subpatterns.size)
            assertEquals(pattern(null, 1), subpatterns.elementAt(0))
            assertEquals(pattern(2, null), subpatterns.elementAt(1))
        }
        it("returns 3 subpatterns for deg 3") {
            val pattern = pattern(2, 3, 1)
            val subpatterns = pattern.subpatterns()

            assertEquals(3, subpatterns.size)
        }
    }

})