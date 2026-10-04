package game//fun game.main() {
//    val liuBei = LiuBei()
//    liuBei.currentHP = liuBei.maxHP
//
//    val caoCao = CaoCao()
//    caoCao.currentHP = caoCao.maxHP
//
//    val sunQuan = SunQuan()
//    sunQuan.currentHP = sunQuan.maxHP
//
//    // Add them to the manager
//    GeneralManager.addGeneral(liuBei)
//    GeneralManager.addGeneral(caoCao)
//    GeneralManager.addGeneral(sunQuan)
//
//    // Print total generals
//    println("Total number of generals: ${GeneralManager.getGeneralCount()}")
//}

//fun game.main() {
//    GeneralManager.getGeneralCount()
//    val generalNames = listOf("LiuBei", "CaoCao", "SunQuan")
//    for (name in generalNames) {
//        val general = GeneralFactory.createGeneral(name)
//        GeneralManager.addGeneral(general)
//    }
//
//    println("Total number of generals: ${GeneralManager.getGeneralCount()}")
//}

// Main.kt
//fun game.main() {
//    GeneralManager.getGeneralCount()
//
//    val lordFactory = LordFactory()
//    val nonLordFactory = NonLordFactory()
//
//    val lord = lordFactory.createRandomGeneral()
//    GeneralManager.addGeneral(lord)
//
//    repeat(2) {
//        val general = nonLordFactory.createRandomGeneral()
//        if (general != null) {
//            GeneralManager.addGeneral(general)
//        }
//    }
//
//    println("Total number of generals: ${GeneralManager.getGeneralCount()}")
//}

// Main.kt

//fun game.main() {
//    GeneralManager.getGeneralCount()
//    GeneralManager.createGenerals(lords = 1, nonLords = 2)
//    println("Total number of generals: ${GeneralManager.getGeneralCount()}")
//
//
//
//}

//fun game.main() {
//    // Create 1 lord and 2 non-lords
//    GeneralManager.createGenerals(lords = 1, nonLords = 2)
//    // Fetch the created list in the same order they were added
//    val created: List<General> = GeneralManager.getGenerals()
//    // Total count
//    println("Total number of generals: ${GeneralManager.getGeneralCount()}")
//
//    // Attack the first
//    created.firstOrNull()?.let { first ->
//        //println("${first.name} is being attacked.")
//        val dodged = first.beingAttacked()
//
//    }
//}
//
//fun game.main() {
//    GeneralManager.createGenerals(lords = 1, nonLords = 2)
//
//    val created = GeneralManager.getPlayers()
//
//    println("Total number of generals: ${GeneralManager.getPlayerCount()}")
//
//    created.firstOrNull()?.let { first ->
//        val equipped = EightTrigrams(first)
//        equipped.beingAttacked()
//    }
//}

fun main() {
    println("--- Game Set-up ---")
    GeneralManager.createGenerals(4)
    val game = GameEngine()
    game.start()
}
