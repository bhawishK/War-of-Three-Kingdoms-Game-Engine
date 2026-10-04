class LoyalistsStrategy() : Strategy() {
    //    override fun whomToAttack() {
//        println("Attack Rebels")
//    }
    override fun whomToAttack(self: General, generals: List<General>): General? {
        return generals.firstOrNull {
            it !== self && it.identity == Identity.REBEL
        }
    }

}