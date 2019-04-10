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
    }

    facts {
        create { 1 implies 2 }
        create { 3 implies 4 }
        create { 1 implies 3 }
        create { 5 implies !1 }
        create { 6 implies !1 }
    }
}