class RebelStrategy() : Strategy() {
    override fun whomToAttack(self: General, generals: List<General>): General? {
        return generals.firstOrNull {
            it !== self && it.identity == Identity.LORD
        }
    }

}