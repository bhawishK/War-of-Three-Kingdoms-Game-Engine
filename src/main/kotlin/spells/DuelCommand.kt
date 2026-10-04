package spells
import General
import cards.CardType

//“Me. You. Outside. Right now."
//Pick any target player (not limited by range)
//to challenge to DUEL, then toss the DUEL card into the discard pile.
//The target player has to use an ATTACK card, after which you have to
//use an ATTACK card as well. The first player to not use an ATTACK card
//receives 1 unit of health damage.
//If the target player does not have a single ATTACK card to begin with,
//or chooses not to use any, he or she immediately receives 1 unit of health damage.
//In that way, the advantage is tilted in favor of the user.

class DuelCommand(
    private val attacker: General,
    private val target: General
) : Command {

    override fun execute() {

        println("[Duel] ${attacker.name} challenges ${target.name}!")

        var attackerTurn = true

        while (true) {

            if (attackerTurn) {
                val hasAttack = attacker.hasAttackCard()

                if (hasAttack) {
                    attacker.removeFirstCardOfType(CardType.ATTACK)
                    println("${attacker.name} plays Attack in Duel.")
                } else {
                    println("${attacker.name} fails to respond.")
                    target.beingAttacked(attacker)
                    break
                }

            } else {
                val hasAttack = target.hasAttackCard()

                if (hasAttack) {
                    target.removeFirstCardOfType(CardType.ATTACK)
                    println("${target.name} plays Attack in Duel.")
                } else {
                    println("${target.name} fails to respond.")
                    target.beingAttacked(attacker)
                    break
                }
            }

            attackerTurn = !attackerTurn
        }
    }
}