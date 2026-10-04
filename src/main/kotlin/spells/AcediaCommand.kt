package spells
import General
import cards.Suit
import kotlin.random.Random

class AcediaCommand(
    private val target: General,
    private val rng : () -> Int ={ Random.Default.nextInt(100) }
) : Command {

    override fun execute() {
        val card = GeneralManager.deck.draw()
        println("Judgement card: ${card.suit} ${card.number}")
        val isHeart = card.suit == Suit.HEART
        if (isHeart) {
            println("[Judgment] ${target.name} dodges Acedia card. No penalty this turn.")
        } else {
            target.skipPlayThisTurn = true
            println("[Judgment] ${target.name} can't dodge the Acedia card.Skipping one round of Play Phase.")
        }
        GeneralManager.deck.discard(card)
    }
}

//The ACEDIA card causes the target player to be unable
//to take any action during his turn. At the "judgement phase"
//of the target player's turn, if the judgement card is any suit
//other than "hearts", the target player has failed the judgement.
//He or she can proceed with the "drawing phase" and draw 2 cards
//from the deck, but is prohibited from doing anything else. The turn
//immediately proceeds to the "discard phase" where the target player
//will have to discard as many cards as necessary.