import cards.CardType

abstract class WeiGeneral(name: String, identity: Identity, strategy: Strategy) : General(name,identity,strategy){
    var nextWei : WeiGeneral? = null

    fun setNext(general: WeiGeneral) : WeiGeneral{
        this.nextWei=general
        return general
    }
    fun handleEntourage(): Boolean {
        val dodgeCard = removeFirstCardOfType(CardType.DODGE)
        if (dodgeCard != null) {
            println("$name helps Cao Cao dodge an attack by spending a Dodge card.")
            GeneralManager.deck.discard(dodgeCard)
            return true
        }

        println("$name failed to help.")
        return nextWei?.handleEntourage() ?: false
    }
}