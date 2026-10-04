class UnhealthyState (private val owner:General,
                        private val strategy: LiuBeiStrategy
):State {

    override fun playNextCard() {
        if(owner.hand.size >=2){
            println("${owner.name} is not healthy.")
            repeat(2) {
                val removed = owner.hand.removeAt(0)
                GeneralManager.deck.discard(removed)
            }

            owner.currentHP +=1
            println("[Benevolence -State] ${owner.name} gives away 2 cards to recover 1 HP, now his HP is ${owner.currentHP}")

        }
        // strategy.updateState()
        if (owner.currentHP >= 2) {
            println("${owner.name} is now healthy.")
        }
    }
}