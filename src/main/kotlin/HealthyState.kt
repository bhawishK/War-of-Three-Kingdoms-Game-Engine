import cards.CardType

class HealthyState(
    private val owner:General,
    private val strategy: LiuBeiStrategy
) : State{


    override fun playNextCard() {
        val opponents = GeneralManager.list.filter { it != owner }

        val target = opponents.firstOrNull {
            it.identity == Identity.REBEL
        }
        val attackCard = owner.removeFirstCardOfType(CardType.ATTACK)
        if (attackCard!=null && target != null) {

            println("[Attack Card - State] ${owner.name} spends a card to attack the rebel, ${target.name}.")
            GeneralManager.deck.discard(attackCard)

            if (owner.strategy is LordStrategy) {
                (owner.strategy as LordStrategy).lordUsedAttack()
            }

            target.beingAttacked()

        } else {
            println("${owner.name} doesn't have attack card.")
        }


    }

}
