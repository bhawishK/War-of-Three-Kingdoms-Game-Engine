import cards.Card
import cards.CardType
import com.sun.org.omg.CORBA.AttributeMode
import spells.Command
import skills.*
interface Player {
    val hand: MutableList<Card>
    val name: String
    val maxHP: Int
    val pendingJudgement : MutableList<Command>
    var skipPlayThisTurn : Boolean
    var currentHP: Int


    fun hasDodgeCard(): Boolean {
        return hand.any { it.type == CardType.DODGE }
    }

    fun hasAttackCard(): Boolean {
        return hand.any { it.type == CardType.ATTACK }
    }
    fun hasPeachCard(): Boolean {
        return hand.any { it.type == CardType.PEACH }
    }
    fun removeFirstCardOfType(cardType: CardType): Card? {
        val index = hand.indexOfFirst { it.type == cardType }
        return if (index >= 0) hand.removeAt(index) else null
    }


    fun dodgeAttack(): Boolean {
        val dodge = removeFirstCardOfType(CardType.DODGE)
        return if (dodge != null) {
            println("$name dodged attack by spending a dodge card.")
            true
        } else {
            false
        }
    }





}

abstract class General(override val name: String,
                        val identity: Identity,
                        var strategy: Strategy) : Player {

    abstract override val maxHP: Int
    override var currentHP: Int = 0
    override val pendingJudgement: MutableList<Command> = mutableListOf()
    override var skipPlayThisTurn: Boolean = false
    override val hand: MutableList<Card> = mutableListOf()
    val skills: MutableList<Skill> = mutableListOf()
    var attackRange : Int = 1 // as the rulebook states
    var attackLimit : Boolean = true
    var plusHorse: Int = 0   // +1 horse (defensive)
    var minusHorse: Int = 0  // -1 horse (offensive)





    init {
        currentHP = maxHP
        println("General $name has been created.")
    }
    fun preparationPhase(){

    }

    override fun dodgeAttack(): Boolean {
        val dodgeCard = removeFirstCardOfType(CardType.DODGE)
        if (dodgeCard != null) {
            val used = dodgeCard.play(this, emptyList())
            if (used) {
                GeneralManager.deck.discard(dodgeCard)
                if (strategy is LordStrategy) {
                    (strategy as LordStrategy).lordDodgeSuccess()
                }
                return true
            }
        }

        if (strategy is LordStrategy) {
            (strategy as LordStrategy).lordDodgeFailed()
        }

        return false
    }

    open fun beingAttacked(attacker: General? = null) {
        println("$name is being attacked.")
        skills.forEach {
            it.onBeingAttacked(this, attacker)
        }

        if (!dodgeAttack()) {
            currentHP--
            println("$name can't dodge the attack, current HP is $currentHP")

            if (currentHP <= 0) {
                onDying(attacker)
            }
        }
    }
    open fun onDying(attacker: General?) {
        println("$name is in dying state.")
        val peach = removeFirstCardOfType(CardType.PEACH)
        if (peach != null) {
            val used = peach.play(this, emptyList())
            if (used) {
                GeneralManager.deck.discard(peach)
            }
        }
        println("$name cannot be saved and dies.")
        GeneralManager.handleDeath(this, attacker)
        return

    }


    fun judgementPhase(){
        // if(pendingJudgement.isNotEmpty()){println("$name has ${pendingJudgement.size} time-delay effect(s) to resolve.")}
        val toExec = pendingJudgement.toList()
        pendingJudgement.clear()
        toExec.forEach { cmd -> cmd.execute() }

    }



    fun whomToAttack(opponents: List<General>,current: General): General? {
        if (opponents.isEmpty()) return null
        return current.strategy.whomToAttack(current,opponents)

    }

    open fun drawPhase() {
        repeat(2) {
            hand.add(GeneralManager.deck.draw())
        }
        println("$name draws 2 cards and now has ${hand.size} card(s).")
    }


    fun playPhase(opponents: List<General>, current: General) {
        // implement this part through card behaviour
        if (skipPlayThisTurn) {
            skipPlayThisTurn = false
            println("$name skips Play Phase.")
            return
        }

        val weapon = removeFirstCardOfType(CardType.ZhugeCrossbow)
        if (weapon != null) {
            val used = weapon.play(current, opponents)
            if (used) GeneralManager.deck.discard(weapon)
        }

        // Lightning
        val lightning = removeFirstCardOfType(CardType.LIGHTNING)
        if (lightning != null) {
            val used = lightning.play(current, opponents)
            if (used) GeneralManager.deck.discard(lightning)
            return
        }

        // Acedia
        val acedia = removeFirstCardOfType(CardType.ACEDIA)
        if (acedia != null) {
            val used = acedia.play(current, opponents)
            if (used) GeneralManager.deck.discard(acedia)
            return
        }

        //  Duel
        val duel = removeFirstCardOfType(CardType.DUEL)
        if (duel != null) {
            val used = duel.play(current, opponents)
            if (used) GeneralManager.deck.discard(duel)
            return
        }

        // peach logic
        val peachCard = removeFirstCardOfType(CardType.PEACH)
        if (peachCard != null) {
            val used = peachCard.play(current, opponents)
            if (used) {
                GeneralManager.deck.discard(peachCard)
            }
            return
        }

        val plusHorse = removeFirstCardOfType(CardType.SHADOW_RUNNER)
        if (plusHorse != null) {
            val used = plusHorse.play(current, opponents)
            if (used) GeneralManager.deck.discard(plusHorse)
        }

        val minusHorse = removeFirstCardOfType(CardType.RED_HARE)
        if (minusHorse != null) {
            val used = minusHorse.play(current, opponents)
            if (used) GeneralManager.deck.discard(minusHorse)
        }


        // attack logic
        if (strategy is LiuBeiStrategy) {
            val liuBeiStrategy = strategy as LiuBeiStrategy

            liuBeiStrategy.updateState()

            if (liuBeiStrategy.isUnhealthy()) {
                liuBeiStrategy.playNextCard()
                return
            }
        }

        if (attackLimit) {

            val attackCard = hand.firstOrNull { it.type == CardType.ATTACK }

            if (attackCard != null) {
                val used = attackCard.play(current, opponents)

                if (used) {
                    hand.remove(attackCard)
                    GeneralManager.deck.discard(attackCard)
                }
            } else {
                println("$name does not have an attack card.")
            }

        } else {

            var attackCard = hand.firstOrNull { it.type == CardType.ATTACK }

            if (attackCard == null) {
                println("$name does not have an attack card.")
            }

            while (attackCard != null) {
                val used = attackCard.play(current, opponents)

                if (used) {
                    hand.remove(attackCard)
                    GeneralManager.deck.discard(attackCard)
                    println("$name attacks again using Zhuge Crossbow!")
                } else {
                    break
                }

                attackCard = hand.firstOrNull { it.type == CardType.ATTACK }
            }
        }

    }
    open fun discardPhase() {
        println("$name has ${hand.size} card(s), current HP is $currentHP.")
        val before = hand.size
        while (hand.size > currentHP) {
            val discarded = hand.removeAt(0)
            GeneralManager.deck.discard(discarded)
        }
        val discardedCount = before - hand.size
        if (discardedCount > 0) {
            println("$name discards $discardedCount card(s) and now has ${hand.size} card(s).")
        } else {
            println("$name does not need to discard.")
        }
    }

    fun finalPhase(){

    }

    fun isDead(): Boolean {
        return currentHP <= 0 || !GeneralManager.list.contains(this)
    }


    fun takeTurn(opponents: List<General>, current: General) {
        if (currentHP <= 0 || !GeneralManager.list.contains(this)) return
        skills.forEach { it.onTurnStart(this) }

        preparationPhase()
        if (isDead()) return

        judgementPhase()
        if (isDead()) return

        drawPhase()
        if (isDead()) return

        playPhase(opponents, current)
        if (isDead()) return

        discardPhase()
        if (isDead()) return

        finalPhase()
    }

}



class LiuBei(identity: Identity, strategy: Strategy)
    : General("Liu Bei", identity, strategy) {

    override val maxHP = 4

    init {
        skills.add(BenevolenceSkill())
    }
}

class CaoCao(identity: Identity, strategy: Strategy)
    : WeiGeneral("Cao Cao", identity, strategy) {

    override val maxHP = 4

    init {
        skills.add(EntourageSkill())
    }
}

class SunQuan(
    identity: Identity,
    strategy: Strategy
) : General("Sun Quan", identity, strategy) {

    override val maxHP: Int = 4
}


