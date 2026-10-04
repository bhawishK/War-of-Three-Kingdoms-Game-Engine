class EightTrigrams(player: Player) : Equipment(player) {

    override fun dodgeAttack(): Boolean {
        println("[Eight Trigrams] Triggered for ${player.name}")

        val judgement = Math.random() < 0.5
        println("Judgement result: $judgement")

        return if (judgement) {
            println("${player.name} dodged the attack using Eight Trigrams.")

            // Notify strategy (important!)
            if (player is General && player.strategy is LordStrategy) {
                (player.strategy as LordStrategy).lordDodgeSuccess()
            }

            true
        } else {
            println("Eight Trigrams failed, fallback to normal dodge.")

            val result = super.dodgeAttack()

            result
        }
    }
}