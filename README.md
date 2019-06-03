## Logical knowledge representation
Solve Analysis problems or Decision making problems by providing LKR.

### Example
First, initialize the knowledge:
```kotlin
val driverKnowledge = knowledge {
    variables {
        create(1, "speed is over 180 kmh")
        create(2, "there is danger")
        create(3, "speed is over 130 kmh")
        create(4, "driver gets a ticket")
        create(5, "engine is 75 hp")
        create(6, "there are 6 people in car")
        inputs(1, 3)
        outputs(2, 4)
    }

    facts {
        fact { 1 implies 2 }
        fact { 3 implies 4 }
        fact { 1 implies 3 }
        fact { 5 implies !1 }
        fact { 6 implies !1 }
    }
}
```

Then you can decompose it, providing `F_u = !⍺1`:
```kotlin
val ap = AnalysisProblem(driverKnowledge) { !ref(1) }
val decomposed = ap.decompose()

decomposed.forEachIndexed { index, layer ->
    if (layer.facts.isNotEmpty()) {
        println("F$index=(${layer.facts.map { it.identifier }.joinToString(",")})")
    }
    println("⍺$index=(${layer.variables.map { it.identifier }.joinToString(",")})")
}
```
which produces (99 is the ID of `F_u`):
```
⍺0=(2,4)
F1=(1,2)
⍺1=(1,3)
F2=(3,4,5,99)
⍺2=(5,6)
```

Or you could solve it:
```kotlin
lateinit var solution: Expr
val time = measureTimeMillis {
    solution = ap.solve()
}
solution.pretty().println()
println("Took $time ms")
```
which produces:
```
(((¬⍺2 ∧ ¬⍺4) ∨ (⍺2 ∧ ¬⍺4)) ∨ (¬⍺2 ∧ ⍺4)) ∨ (⍺2 ∧ ⍺4)
Took 26 ms
```


## Association rules discovery
Discover patterns and rules by providing desired minimum support and confidence.

### Example
Let's initialize the knowledge, having 7 rows, each with 3 attributes:
```kotlin
val knowledge = Knowledge(
    listOf(1,2,1),
    listOf(2,0,0),
    listOf(2,3,0),
    listOf(2,1,1),
    listOf(2,2,0),
    listOf(3,3,1),
    listOf(2,1,1)
)
```

Then we could get the patterns of degree 1:
```kotlin
val patterns = knowledge.getSimplePatterns()
patterns.forEach(::println)
```

```
[1, _, _]
[2, _, _]
[3, _, _]
[_, 0, _]
[_, 1, _]
[_, 2, _]
[_, 3, _]
[_, _, 0]
[_, _, 1]
```

Or we could discover patterns that satisfy minimum support:
```kotlin
val patterns = knowledge.discoverProperties(support = 2/7.0).sortedBy(knowledge::supportFor)
patterns.forEach(::println)
```

```
[2, 1, 1]
[2, 1, _]
[2, _, 1]
[_, 1, 1]
[2, _, 0]
```

Finally, we could discover rules satisfying minimum support and confidence:
```kotlin
val rules = knowledge.discoverRules(support = 2/7.0, confidence = 1.0).sortedBy(knowledge::liftFor)
println("Found ${rules.size} rules")
rules.forEach { println("$it (s = ${"%.2f".format(knowledge.supportFor(it))}, c = ${"%.2f".format(knowledge.confidenceFor(it))}, l = ${"%.2f".format(knowledge.liftFor(it))})") }
```

```
Found 7 rules
x(1)=1 ∧ x(2)=1 ⇒ x(0)=2 (s = 0.29, c = 1.00, l = 1.40)
x(1)=1 ⇒ x(0)=2 (s = 0.29, c = 1.00, l = 1.40)
x(2)=0 ⇒ x(0)=2 (s = 0.43, c = 1.00, l = 1.40)
x(0)=2 ∧ x(1)=1 ⇒ x(2)=1 (s = 0.29, c = 1.00, l = 1.75)
x(1)=1 ⇒ x(2)=1 (s = 0.29, c = 1.00, l = 1.75)
x(1)=1 ⇒ x(2)=1 ∧ x(0)=2 (s = 0.29, c = 1.00, l = 3.50)
x(2)=1 ∧ x(0)=2 ⇒ x(1)=1 (s = 0.29, c = 1.00, l = 3.50)

```