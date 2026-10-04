package cards

object DeckConfig {

    fun getCardSpecs(): List<CardSpec> {
        val specs = mutableListOf<CardSpec>()

        val suits = listOf(
            Suit.SPADE,
            Suit.HEART,
            Suit.DIAMOND,
            Suit.CLUB
        )

        // ATTACK cards
        for (suit in suits) {
            for (number in 1..6) {
                specs.add(CardSpec(CardType.ATTACK, suit, number))
            }
        }

        // DODGE cards
        for (suit in suits) {
            for (number in 7..9) {
                specs.add(CardSpec(CardType.DODGE, suit, number))
            }
        }

        // PEACH cards
        for (suit in suits) {
            for (number in 10..11) {
                specs.add(CardSpec(CardType.PEACH, suit, number))
            }
        }

        // ACEDIA
        specs.add(CardSpec(CardType.ACEDIA, Suit.SPADE, 12))
        specs.add(CardSpec(CardType.ACEDIA, Suit.CLUB, 13))
        // LIGHTNING
        specs.add(CardSpec(CardType.LIGHTNING, Suit.SPADE, 1))
        specs.add(CardSpec(CardType.LIGHTNING, Suit.SPADE, 2))
        // DUEL
        specs.add(CardSpec(CardType.DUEL, Suit.HEART, 3))
        specs.add(CardSpec(CardType.DUEL, Suit.DIAMOND, 4))
        specs.add(CardSpec(CardType.DUEL, Suit.CLUB, 5))
        // ZhugeCrossbow
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.DIAMOND, 4))
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.CLUB, 5))
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.SPADE, 12))
        // +1 Horse (Shadowrunner)
        specs.add(CardSpec(CardType.SHADOW_RUNNER, Suit.SPADE, 13))
        specs.add(CardSpec(CardType.SHADOW_RUNNER, Suit.CLUB, 12))
        // -1 Horse (Red Hare)
        specs.add(CardSpec(CardType.RED_HARE, Suit.HEART, 1))
        specs.add(CardSpec(CardType.RED_HARE, Suit.DIAMOND, 13))


        return specs
    }
}