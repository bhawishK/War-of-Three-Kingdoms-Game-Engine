package cards

object CardFactory {
    fun create(type: CardType, suit: Suit, number: Int): Card {
        println("[CardFactory] Creating $type")
        return when (type) {
            CardType.ATTACK -> AttackCard(suit, number)
            CardType.DODGE -> DodgeCard(suit, number)
            CardType.PEACH -> PeachCard(suit, number)
            CardType.ACEDIA -> AcediaCard(suit, number)
            CardType.LIGHTNING -> LightningCard(suit, number)
            CardType.DUEL -> DuelCard(suit, number)
            CardType.ZhugeCrossbow -> ZhugeCrossbow(suit,number)
            CardType.SHADOW_RUNNER-> ShadowRunner(suit,number)
            CardType.RED_HARE-> RedHare(suit,number)
        }
    }
}