package com.tsovedenski.knowledge

/**
 * Created by Tsvetan Ovedenski on 2019-04-10.
 */
val wellnessKnowledge = knowledge {
    variables {
        create(1, "sleep longer than 8 hrs")
        create(2, "feel well")
        create(3, "diet is poor")
        create(4, "need to go to the doctor")
    }

    facts {
        create { 1 implies 2 }
        create { 3 implies 4 }
        create { ref(2) or 4 }
    }
}

val cakeKnowledge = knowledge {
    variables {
        create(1, "cooking time is bigger than 40 mins")
        create(2, "cooking time is smaller than 60 mins")
        create(3, "cake is tasty")
        create(4, "cake is inedible")
        inputs(1, 2)
        outputs(3, 4)
    }

    facts {
        create { (ref(1) and 2) implies 3 }
        create { (!1 or !2) implies 4 }
        create { 4 implies !3 }
    }
}

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
        create { 1 implies 2 }
        create { 3 implies 4 }
        create { 1 implies 3 }
        create { 5 implies !1 }
        create { 6 implies !1 }
    }
}

val plantKnowledge = knowledge {
    variables {
        create(1, "the first valve is open")
        create(2, "the second valve is closed")
        create(3, "the process state is normal")
        create(4, "product temperature is low")
        create(5, "pressure is high")
        create(6, "operation O1 is executed after O2")
        create(7, "humidity is lower than 50%")
        create(8, "operation O2 is executed after O1")
        create(9, "humidity is 40%")
        inputs(1, 2, 6, 8)
        outputs(3)
    }

    facts {
        create { ref(1) and ref(2) implies ref(3) }
        create { (ref(4) and ref(3)) or (ref(5) and !ref(3)) }
        create { ref(6) implies ref(7) }
        create { (!ref(2) and !ref(7) or (ref(8) and ref(9))) }
        create { (ref(1) and !ref(9)) implies !ref(5) }
    }
}

val randomKnowledge = knowledge {
    variables {
        (1..10).forEach { create(it) }
        outputs(9, 10)
    }

    facts {
        create { ref(3) and ref(10) }
        create { ref(4) or ref(9) }
        create { ref(4) implies (ref(5) and ref(6)) }
        create { ref(5) or ref(6) implies ref(3) }
        create { ref(8) implies ref(6) }
        create { (ref(5) or ref(7)) implies !ref(6) }
        create { ref(1) or ref(2) or ref(8) }
    }
}