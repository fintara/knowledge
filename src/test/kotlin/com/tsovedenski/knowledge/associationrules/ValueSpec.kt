package com.tsovedenski.knowledge.associationrules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.describe

/**
 * Created by Tsvetan Ovedenski on 2019-05-30.
 */
object ValueSpec : Spek({
    describe("degree") {
        it("returns degree 1 for pattern size 3") {
            val pattern = pattern(null, 1, null)
            assertEquals(3, pattern.size)
            assertEquals(1, pattern.degree)
        }
        it("returns degree 2 for pattern size 3") {
            val pattern = pattern(2, null, 1)
            assertEquals(3, pattern.size)
            assertEquals(2, pattern.degree)
        }
        it("returns degree 3 for pattern size 3") {
            val pattern = pattern(2, 1, 1)
            assertEquals(3, pattern.size)
            assertEquals(3, pattern.degree)
        }
    }
})