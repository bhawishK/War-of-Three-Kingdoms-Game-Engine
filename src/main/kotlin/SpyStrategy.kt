class SpyStrategy (private val owner:General ) :Strategy(),Observer {
    private var riskLevel: Double = 50.0
    override fun whomToAttack(self: General, generals: List<General>): General? {
        return generals.firstOrNull {
            it !== self && it.identity == Identity.REBEL
        }
    }
    override fun update(event: LordEvent) {
        when (event) {
            LordEvent.DODGE_FAILED -> {
                riskLevel *= 1.5
                //println("[Spy] Lord failed to dodge. Risk increased to $riskLevel")
            }

            LordEvent.DODGE_SUCCESS -> {
                riskLevel *= 0.5
            }
            LordEvent.ATTACK_USED->{
                // nothing for now
            }
        }
        println("${owner.name} on Lord's Risk Level: ${riskLevel.toInt()}")
    }

    fun getRiskLevel(): Double {
        return riskLevel
    }


}