import cards.Card
import spells.Command

abstract class Equipment(
    protected val player: Player
) : Player {

    override val name: String
        get() = player.name

    override val maxHP: Int
        get() = player.maxHP

    override var currentHP: Int
        get() = player.currentHP
        set(value) {
            player.currentHP = value
        }

    override val hand: MutableList<Card>
        get() = player.hand

    override val pendingJudgement: MutableList<Command>
        get() = player.pendingJudgement

    override var skipPlayThisTurn: Boolean
        get() = player.skipPlayThisTurn
        set(value) {
            player.skipPlayThisTurn = value
        }

    override fun dodgeAttack(): Boolean {
        return player.dodgeAttack()
    }
}