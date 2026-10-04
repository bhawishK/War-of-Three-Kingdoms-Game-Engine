open class LordStrategy : Strategy(),Subject {
    private val observers = mutableListOf<Observer>()

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers(event: LordEvent) {
        for (observer in observers) {
            observer.update(event)
        }
    }

    fun lordUsedAttack() {
        notifyObservers(LordEvent.ATTACK_USED)
    }

    fun lordDodgeSuccess() {
        notifyObservers(LordEvent.DODGE_SUCCESS)
        //println("DODGE TACK HAS BEEN OBSERVED")

    }

    fun lordDodgeFailed() {
        notifyObservers(LordEvent.DODGE_FAILED)
    }



    //    override fun whomToAttack(players: List<Player>, self: Player): Player? {
//        return players.firstOrNull {
//            it !== self && it is General && it.identity == Identity.REBEL
//        }
//    }
    override fun whomToAttack(self: General, generals: List<General>): General? {
        return generals.firstOrNull {
            it !== self && it.identity == Identity.REBEL
        }
    }
}
