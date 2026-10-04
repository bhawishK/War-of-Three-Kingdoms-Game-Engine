package spells
import General
import cards.Suit

class LightningCommand (private  val target: General) : Command {
    override fun execute() {
        println("[Judgement] ${target.name} resolves Lightning.")
        val card = GeneralManager.deck.draw()
        println("Judgement card: ${card.suit} ${card.number}")
        val isSpade = card.suit == Suit.SPADE
        val inRange = card.number in 2..9
        if (isSpade && inRange) {
            println("[Lightning] ${target.name} takes 3 damage!")
            target.currentHP -= 3
            if (target.currentHP <= 0) {
                target.onDying(null)
            }
        } else {
            println("[Lightning] passes to next player.")
            val next = getNextPlayer(target)
            next.pendingJudgement.add(LightningCommand(next))
        }
        GeneralManager.deck.discard(card)
    }
    private fun getNextPlayer(current: General): General {
        val list = GeneralManager.list
        val index = list.indexOf(current)
        return list[(index + 1) % list.size]
    }

}