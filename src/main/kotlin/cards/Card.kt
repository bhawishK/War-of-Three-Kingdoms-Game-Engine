package cards
import spells.*
import spells.AcediaCommand
import General
import spells.LightningCommand
import LordStrategy
import equipment.*

abstract class Card(
    val name: String,
    val type: CardType,
    val suit: Suit,
    val number: Int
) {
    override fun toString(): String {
        return "$name ($suit $number)"
    }
    abstract fun play(source: General, opponents: List<General>): Boolean
}

class AttackCard(suit: Suit, number: Int) :
    Card("Attack", CardType.ATTACK, suit, number) {
    override fun play(source: General, opponents: List<General>): Boolean {

        val target = source.strategy.whomToAttack(source, opponents)?.takeIf {
            val dist = GeneralManager.getDistance(source, it)
            dist <= source.attackRange
        }
        if (target != null) {
            println("[Attack] ${source.name} spends an Attack card to attack a ${target.identity}, ${target.name}")
            if (source.strategy is LordStrategy) {
                (source.strategy as LordStrategy).lordUsedAttack()
            }
            target.beingAttacked(source)
            return true
        }
        println("${source.name} has no valid target.")
        source.hand.add(this)
        return false
    }
}

class DodgeCard(suit: Suit, number: Int) :
    Card("Dodge", CardType.DODGE, suit, number) {
    override fun play(source: General, opponents: List<General>): Boolean {
        println("[Dodge] ${source.name} dodged attack by spending a Dodge card.")
        return true
    }
}

class PeachCard(suit: Suit, number: Int) :
    Card("Peach", CardType.PEACH, suit, number) {
    override fun play(source: General, opponents: List<General>): Boolean {
        if (source.currentHP >= source.maxHP) {
            //println("${source.name} is already at full HP.")
            source.hand.add(this)
            return false
        }

        source.currentHP++

        println("[Peach] ${source.name} uses Peach and recovers 1 HP (HP = ${source.currentHP})")

        return true
    }
}

class AcediaCard(suit: Suit, number: Int) :
    Card("Acedia", CardType.ACEDIA, suit, number) {

    fun toCommand(target: General): Command {
        return AcediaCommand(target)
    }

    override fun play(source: General, opponents: List<General>): Boolean {
        val target = opponents.firstOrNull() ?: return false
        println("[Acedia] ${source.name} places Acedia on ${target.name}")
        target.pendingJudgement.add(this.toCommand(target))
        return true
    }
}

class LightningCard(suit: Suit, number: Int) : Card("Lightning", CardType.LIGHTNING,suit,number) {
    fun toCommand(target: General): Command {
        return LightningCommand(target)
    }

    override fun play(source: General, opponents: List<General>): Boolean {
        //println("[Lightning] ${source.name} places Lightning on ${source.name}")
        source.pendingJudgement.add(this.toCommand(source))
        return true
    }
}

class DuelCard(suit: Suit, number: Int) : Card("DUEL", CardType.DUEL, suit, number) {
    fun toCommand(source: General, target: General): Command {
        return DuelCommand(source, target)
    }
    override fun play(source: General, opponents: List<General>): Boolean {
        val target = opponents.firstOrNull() ?: return false
        //println("[Duel] ${source.name} challenges ${target.name}!")
        DuelCommand(source, target).execute()
        return true
    }
}



// Equipment Cards -- Weapon

// Zhu Ge Crossbow 诸葛连弩 (zhū gě lián nǔ)
// During the action phase of your turn, you can use as many ATTACK cards as you wish.
// ZhugeCrossbow
class ZhugeCrossbow(suit: Suit, number: Int) : Card("ZhugeCrossbow", CardType.ZhugeCrossbow, suit, number) {
    fun toCommand(source: General): Command {
        return ZhugeCrossbowCommand(source)
    }
    override fun play(source: General, opponents: List<General>): Boolean {
        ZhugeCrossbowCommand(source).execute()
        return true
    }
}


// Equipment Cards -- Armor

// EightTrigrams 八卦阵 (bā guà zhèn)
//Every time there is a need to use DODGE 闪, EIGHT TRIGRAMS
//can automatically use DODGE for you. Judgement needs to be passed
//for it to work. If the judgement card is a red suit (hearts or diamonds),
//the attack is automatically dodged.


// Eqipment Cards  -- Horse Cards

// +1 Horse
// 【Shadowrunner】 The Shadow 绝影 (jué yǐng)
class ShadowRunner(suit: Suit, number: Int) :
    Card("ShadowRunner", CardType.SHADOW_RUNNER, suit, number) {

    override fun play(source: General, opponents: List<General>): Boolean {
        source.plusHorse = 1
        println("[ShadowRunner] ${source.name} equiped with +1 Horse")

        return true
    }
}

// -1 Horse
// Red Hare

class RedHare(suit: Suit, number: Int) :
    Card("RedHare", CardType.RED_HARE, suit, number) {

    override fun play(source: General, opponents: List<General>): Boolean {
        source.minusHorse = 1
        println("[Red Hare] ${source.name} equiped with -1 Horse")
        //println("${source.name} equips Red Hare (-1 Horse).")
        return true
    }
}