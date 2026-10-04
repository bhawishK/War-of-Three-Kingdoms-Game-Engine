package game
import cards.Card
import cards.CardFactory
import cards.DeckConfig

class Deck {
    private val drawPile: MutableList<Card> = mutableListOf()
    private val discardPile: MutableList<Card> = mutableListOf()

    fun initialize() {
        val specs = DeckConfig.getCardSpecs()

        for (spec in specs) {
            val card = CardFactory.create(spec.type, spec.suit, spec.number)
            drawPile.add(card)
        }

        drawPile.shuffle()
    }

    fun draw(): Card {
        if (drawPile.isEmpty()) reshuffle()
        return drawPile.removeAt(0)
    }

    fun discard(card: Card) {
        discardPile.add(card)
    }

    private fun reshuffle() {
        drawPile.addAll(discardPile)
        discardPile.clear()
        drawPile.shuffle()
    }
}