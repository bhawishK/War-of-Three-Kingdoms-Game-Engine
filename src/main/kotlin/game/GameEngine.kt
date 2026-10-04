package game
import cards.*
import GameResult

class GameEngine {
    private val players = GeneralManager.list
    private val deck = GeneralManager.deck
    
    private fun checkResult() : GameResult{
        val alive = players.filter { it.currentHP > 0 }
        val lords = alive.any{ it.identity == Identity.LORD}
        val rebels = alive.any { it.identity == Identity.REBEL }
        val spies = alive.any { it.identity == Identity.SPY }
        if(!lords){return GameResult.REBEL_SIDE_WINS}
        if(!rebels){return GameResult.LORD_SIDE_WINS}
        if(alive.size == 1 && spies){return GameResult.SPY_WINS}
        return GameResult.ONGOING
    }

    fun start(){
        println("--- Game Start ---")
        println("Total number of players: ${players.size}")
        // Lightning Card Test Output
        //val lightning = CardFactory.create(CardType.LIGHTNING, Suit.SPADE, 1) as LightningCard
        //players[0].pendingJudgement += lightning.toCommand(players[0])

        deck.initialize()


        // Duel Card Test Output
//        val p1 = players[0]
//        val p2 = players[1]
//        val duelCard = DuelCard(Suit.SPADE, 1)
//        val command = duelCard.toCommand(p1, p2)
//
//        command.execute()

        while(checkResult() == GameResult.ONGOING){
            for(general in players.toList()){
                if(general.currentHP > 0 ){
                    val others = players.filter {
                        it != general && it.currentHP > 0
                    }
                    general.takeTurn(others,general)
                    val result = checkResult()
                    if(result!=GameResult.ONGOING){
                        println("Game Over: $result ")
                        println("--- Game End ---")
                        return
                    }
                }
            }
            println("---Next Round---")
        }





    }

}
