import cards.AcediaCard
import game.Deck
import cards.CardFactory
import cards.CardType
import cards.Suit

object GeneralManager {
    val list: MutableList<General> = mutableListOf()
    val deck =  Deck()

    init {
        println("Setting up the general manager.")
    }

    fun addGeneral(general: General) {
        println("${general.name}, a ${general.identity} (with ${general.currentHP} HP) has been added to the list.")
        list.add(general)
    }

    fun getPlayerCount(): Int = list.size

    fun generateRoles(totalPlayers: Int): MutableList<Identity> {

        val roles = when (totalPlayers) {

            4 -> listOf(
                Identity.LORD,
                Identity.LOYALIST,
                Identity.REBEL,
                Identity.SPY
            )

            5 -> listOf(
                Identity.LORD,
                Identity.LOYALIST,
                Identity.REBEL,
                Identity.REBEL,
                Identity.SPY
            )

            6 -> listOf(
                Identity.LORD,
                Identity.LOYALIST,
                Identity.REBEL,
                Identity.REBEL,
                Identity.REBEL,
                Identity.SPY
            )

            7 -> listOf(
                Identity.LORD,
                Identity.LOYALIST,
                Identity.LOYALIST,
                Identity.REBEL,
                Identity.REBEL,
                Identity.REBEL,
                Identity.SPY
            )

            8 -> listOf(
                Identity.LORD,
                Identity.LOYALIST,
                Identity.LOYALIST,
                Identity.REBEL,
                Identity.REBEL,
                Identity.REBEL,
                Identity.REBEL,
                Identity.SPY
            )

            else -> throw IllegalArgumentException("Unsupported number of players")
        }

        return roles.toMutableList()
    }


    fun createGenerals(totalPlayers: Int) {
        val roles = generateRoles(totalPlayers)
        val lordFactory = LordFactory()
        var caoCao: CaoCao? = null
        val lordIndex = roles.indexOf(Identity.LORD)
        if (lordIndex >= 0) {
            val lord = lordFactory.createRandomGeneral(Identity.LORD)
            if (lord != null) {
                addGeneral(lord)

                if (lord is CaoCao) {
                    caoCao = lord
                }
            }
        }
        val nonLordFactory = NonLordFactory(caoCao, totalPlayers)
        for (role in roles.withIndex()) {
            if (role.index == lordIndex) continue
            val general = nonLordFactory.createRandomGeneral(role.value)
            if (general != null) {
                addGeneral(general)
            }
        }
        val lord = list.firstOrNull { it.identity == Identity.LORD }

        if (lord != null && lord.strategy is LordStrategy) {
            val lordStrategy = lord.strategy as LordStrategy

            for (general in list) {
                if (general.strategy is SpyStrategy) {
                    lordStrategy.registerObserver(general.strategy as SpyStrategy)
                    println("${general.name} is observing lord.")
                }
            }
        }
    }



    fun getPlayers(): List<Player> = list.toList()

    private fun dealInitialCards() {
        repeat(4) {
            for (general in list) {
                general.hand.add(deck.draw())
                println("cards.Card has been drawn to :${general}")
            }
        }
    }
    fun handleDeath(deadGeneral: General, killer: General?) {
        list.remove(deadGeneral)
        println("${deadGeneral.name} is removed from the game.")
        if(deadGeneral.identity == Identity.REBEL && killer !=null){
            repeat(3){
                killer.hand.add(deck.draw())
            }
            println("[Reward] ${killer.name} draws 3 cards for killing a Rebel.")        }

        if(deadGeneral.identity == Identity.LOYALIST && killer?.identity == Identity.LORD){
            println("[Punishment] ${killer.name} killed a Loyalist and must discard all cards.")
            val discardedCount = killer.hand.size
            killer.hand.clear()
            println("${killer.name} discards $discardedCount card(s).")

        }
    }
    fun calculateBaseDistance(from: General, to: General): Int {
        // Circular Distance Approach
        val list = list
        val n = list.size

        val fromIndex = list.indexOf(from)
        val toIndex = list.indexOf(to)

        val clockwise = (toIndex - fromIndex + n) % n
        val counter = (fromIndex - toIndex + n) % n

        return minOf(clockwise, counter)
    }
    fun getDistance(source: General, target: General): Int {
        val baseDistance = calculateBaseDistance(source, target)

        val modifiedDistance = baseDistance
        + target.plusHorse
        - source.minusHorse

        println("Distance from ${source.name} to ${target.name} = $modifiedDistance")

        return if (modifiedDistance < 1) 1 else modifiedDistance
    }


//    fun checkGameResult(): GameResult {
//        val lordAlive = list.any { it.identity == Identity.LORD && it.currentHP > 0 }
//        val rebelsAlive = list.any { it.identity == Identity.REBEL && it.currentHP > 0 }
//
//        if (!lordAlive) return GameResult.REBEL_SIDE_WINS
//        if (!rebelsAlive) return GameResult.LORD_SIDE_WINS
//
//        return GameResult.ONGOING
//    }
    // gameStart logic moved to GameEngine

//    fun gameStart() {
//        println("Total number of players: ${list.size}")
//        println()
//
//        // identities already established
//
//        deck.initialize() // cards made
//
//        if (list.size >= 4) {
//            val target = list[3]
//            val acediaCard = CardFactory.create(CardType.ACEDIA, Suit.SPADE, 1) as AcediaCard
//            target.pendingJudgement += acediaCard.toCommand(target)
//            println("${target.name} being placed the Acedia card")
//        }
//        while (checkGameResult() == GameResult.ONGOING) {
//            for (general in list.toList()) {
//                if (general.currentHP > 0) {
//                    val others = list.filter { it != general && it.currentHP > 0 }
//                    general.takeTurn(others, general)
//
//                    val result = checkGameResult()
//                    if (result != GameResult.ONGOING) {
//                        println("Game Over: $result")
//                        return
//                    }
//                }
//            }
//            println("\n--- Next Round ---\n")
//        }
//    }

}

//object GeneralManager {
//    private val players = mutableListOf<Player>()
//
//    fun addPlayer(player: Player) {
//        players.add(player)
//    }
//
//    fun getPlayers(): List<Player> = players
//}
