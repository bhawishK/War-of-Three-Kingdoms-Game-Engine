## 1. Cards: Factory Method Pattern, Deck, and Configuration Design

### Explanation

To implement different cards, the deck construction is designed using a factory-based approach to ensure clean separation between **data, object creation, and game logic**.

The `Card` class acts as an abstract base class, while concrete subclasses such as `AttackCard`, `DodgeCard`, `PeachCard`, `AcediaCard`, 'etc' represent specific card types in the game.

To avoid tightly coupling the `Deck` class with these concrete card implementations, a `CardFactory` is introduced. The factory provides a method `create(type, suit, number)` that returns the appropriate `Card` subclass based on the input parameters.

An important addition in my implementation is that the deck composition is defined separately using `DeckConfig`.
---

### Code Snippet
```kotlin
enum class CardType {
    ATTACK,
    DODGE,
    PEACH,
    ACEDIA,
    LIGHTNING,
    DUEL,
    ZhugeCrossbow,
    SHADOW_RUNNER,
    RED_HARE
}

```

```kotlin
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
        println("${source.name} dodged attack by spending a Dodge card.")
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
```
```kotlin
object CardFactory {
    fun create(type: CardType, suit: Suit, number: Int): Card {
        println("[CardFactory] Creating $type")
        return when (type) {
            CardType.ATTACK -> AttackCard(suit, number)
            CardType.DODGE -> DodgeCard(suit, number)
            CardType.PEACH -> PeachCard(suit, number)
            CardType.ACEDIA -> AcediaCard(suit, number)
            CardType.LIGHTNING -> LightningCard(suit, number)
            CardType.DUEL -> DuelCard(suit, number)
            CardType.ZhugeCrossbow -> ZhugeCrossbow(suit,number)
            CardType.SHADOW_RUNNER-> ShadowRunner(suit,number)
            CardType.RED_HARE-> RedHare(suit,number)
        }
    }
}
```
```kotlin
object DeckConfig {

    fun getCardSpecs(): List<CardSpec> {
        val specs = mutableListOf<CardSpec>()

        val suits = listOf(
            Suit.SPADE,
            Suit.HEART,
            Suit.DIAMOND,
            Suit.CLUB
        )

        // ATTACK cards
        for (suit in suits) {
            for (number in 1..6) {
                specs.add(CardSpec(CardType.ATTACK, suit, number))
            }
        }

        // DODGE cards
        for (suit in suits) {
            for (number in 7..9) {
                specs.add(CardSpec(CardType.DODGE, suit, number))
            }
        }

        // PEACH cards
        for (suit in suits) {
            for (number in 10..11) {
                specs.add(CardSpec(CardType.PEACH, suit, number))
            }
        }

        // ACEDIA
        specs.add(CardSpec(CardType.ACEDIA, Suit.SPADE, 12))
        specs.add(CardSpec(CardType.ACEDIA, Suit.CLUB, 13))
        // LIGHTNING
        specs.add(CardSpec(CardType.LIGHTNING, Suit.SPADE, 1))
        specs.add(CardSpec(CardType.LIGHTNING, Suit.SPADE, 2))
        // DUEL
        specs.add(CardSpec(CardType.DUEL, Suit.HEART, 3))
        specs.add(CardSpec(CardType.DUEL, Suit.DIAMOND, 4))
        specs.add(CardSpec(CardType.DUEL, Suit.CLUB, 5))
        // ZhugeCrossbow
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.DIAMOND, 4))
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.CLUB, 5))
        specs.add(CardSpec(CardType.ZhugeCrossbow, Suit.SPADE, 12))
        // +1 Horse (Shadowrunner)
        specs.add(CardSpec(CardType.SHADOW_RUNNER, Suit.SPADE, 13))
        specs.add(CardSpec(CardType.SHADOW_RUNNER, Suit.CLUB, 12))
        // -1 Horse (Red Hare)
        specs.add(CardSpec(CardType.RED_HARE, Suit.HEART, 1))
        specs.add(CardSpec(CardType.RED_HARE, Suit.DIAMOND, 13))


        return specs
    }
}
```
---

### UML Diagram 

![UML Diagram](CardFactory_UML.png)

---

### Sample Output 
![Sample](CardFactory_Sample.png)

```text 

```

---

## 2. Skills: Observer Pattern

###  Explanation

The skill system is implemented using an event-driven architecture based on the Observer Pattern.

---

###  Code Snippet 

```kotlin
interface Skill {
    val name : String
    fun onTurnStart(owner:General){}
    fun onBeingAttacked(owner:General,attacker:General?){}
    fun onAfterDodge(owner:General){}
    fun onAttack(owner:General, target: General){}
}
class EntourageSkill : Skill {

    override val name = "Entourage"

    override fun onBeingAttacked(owner: General, attacker: General?) {

        val wei = owner as? WeiGeneral ?: return

        println("[Entourage] ${owner.name} activates Entourage.")

        val mitigated = wei.nextWei?.handleEntourage() ?: false

        if (mitigated) {
            println("${owner.name} was protected by Wei generals.")
        }
    }
}
class BenevolenceSkill : Skill {

    override val name = "Benevolence"

    override fun onTurnStart(owner: General) {

        if (owner.currentHP < 2 && owner.hand.size >= 2) {

            owner.hand.removeAt(0)
            owner.hand.removeAt(0)
            owner.currentHP += 1

            println("[Benevolence] ${owner.name} gives 2 cards to recover 1 HP.")
        }
    }
}
class BeautySkill : Skill {

    override val name = "Beauty Outshining the Moon"

    override fun onTurnStart(owner: General) {

        val card = GeneralManager.deck.draw()
        owner.hand.add(card)

        println("[Beauty] ${owner.name} draws 1 extra card.")
    }
}
```

---

### UML Diagram

![UML Diagram](Skills_UML.png)

---

### Sample Output 
![Sample](Skills_Sample.png)

```text
```

---

## 3. Game Engine

### Explanation

GameEngine controls the flow of the game and separates logic from GeneralManager.

---

### Code Snippet (Placeholder)

```kotlin
class GameEngine {
    private val players = GeneralManager.list
    private val deck = GeneralManager.deck

    fun start() {
        println("Total number of players: ${players.size}")
        println()

        deck.initialize()

        while (GeneralManager.checkGameResult() == GameResult.ONGOING) {
            for (general in players.toList()) {
                if (general.currentHP > 0) {
                    val others = players.filter {
                        it != general && it.currentHP > 0
                    }
                    general.takeTurn(others, general)
                    val result = GeneralManager.checkGameResult()
                    if (result != GameResult.ONGOING) {
                        println("Game Over: $result ")
                        return
                    }
                }
            }
            println("---Next Round---")
        }
    }
}





    
```

---

### UML Diagram 

![UML Diagram](GameEngine_UML.png)

---

###  Sample Output
![Sample](GameEngine_Sample.png)


## 4. Cards : Command Pattern

### Explanation 

The judgment phase includes time-delay spell cards such as Acedia,Lightning,etc. These cards are not supposed
to be played immediately hence they are placed on the player first and then resolved during the judgement phase.
In my game-engine, I am placing these cards myself to demonstrate the use case.

Command Pattern can be utilized for the implementation as it allows creation of separate cards from their execution logic.
Hence, we do not have to create special cards but utilize our existing CardFactory to produce these cards and only change 
their execution. Hence, we utilize Command (interface) and create concrete Command logic i.e LigthningCommand.

This approach provides loose coupling between cards and their specified logic. Cards can be created without thinking of the 
logic making the development and maintenance  easier. It also provides Extensibility as new spell cards can be added at later
stages of the game. Therefore, this approach  separate game flow, card logic and effects. 

In addition to delayed spell cards such as Acedia and Lightning, immediate spell cards such as Duel are also implemented.
Unlike delayed cards, Duel executes instantly and forces two players into a turn-based exchange of attack cards.
This demonstrates that the Command Pattern is flexible enough to support both delayed and immediate card effects
within the same system.
### Code Snippet

```kotlin
interface Command {
    fun execute()
}

class LightningCommand (private  val target: General) : Command {
    override fun execute() {
        println("[Judgement] ${target.name} resolves Lightning.")
        val card = GeneralManager.deck.draw()
        println("Judgement card: ${card.suit} ${card.number}")
        val isSpade = card.suit == Suit.SPADE
        val inRange = card.number in 2..9
        if (isSpade && inRange) {
            println("[Lightning] ${target.name} takes 3 damage!")
            target.currentHP -= 3
            if (target.currentHP <= 0) {
                target.onDying(null)
            }
        } else {
            println("[Lightning] passes to next player.")
            val next = getNextPlayer(target)
            next.pendingJudgement.add(LightningCommand(next))
        }
        GeneralManager.deck.discard(card)
    }
    private fun getNextPlayer(current: General): General {
        val list = GeneralManager.list
        val index = list.indexOf(current)
        return list[(index + 1) % list.size]
    }

}

class AcediaCommand(
    private val target: General,
    private val rng : () -> Int ={ Random.Default.nextInt(100) }
) : Command {

    override fun execute() {
        val card = GeneralManager.deck.draw()
        println("Judgement card: ${card.suit} ${card.number}")
        val isHeart = card.suit == Suit.HEART
        if (isHeart) {
            println("[Judgment] ${target.name} dodges Acedia card. No penalty this turn.")
        } else {
            target.skipPlayThisTurn = true
            println("[Judgment] ${target.name} can't dodge the Acedia card.Skipping one round of Play Phase.")
        }
        GeneralManager.deck.discard(card)
    }
}

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


```
```kotlin
class AttackCard(suit: Suit, number: Int) :
    Card("Attack", CardType.ATTACK, suit, number) {
    override fun play(source: General, opponents: List<General>): Boolean {

        val target = opponents.firstOrNull {
            val dist = GeneralManager.getDistance(source, it)
            //println("Distance from ${source.name} to ${it.name} = $dist")
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
        println("${source.name} dodged attack by spending a Dodge card.")
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
```
### UML Diagram
![UML Diagram](Command_UML.png)

### Sample Output
![Sample](Cards_Sample.png)


## 5. Distance System + Horses

### Explanation

A simple distance system is implemented to restrict attacks based on player positions. Players are arranged in a circular structure, and the distance between two players is defined as the shortest path (clockwise or counterclockwise) between them.

Each player has a default attack range of 1, meaning they can only attack adjacent players.

Distance between players, as described in the rulebook is used for restricting attack based on player position. 
The Players are arranged in circle and each has attacking range of 1 so only adjacent player can be attack. In this we 
had to find the shortest distance for each player to attack and restrict attack.
I used formulas and calculate the clockwise distance, anticlockwise distance hence distance from player to left and to right.
Then I took the minimum of both and returned it. Before attacking, it uses dist <= current.attackRange to check whether 
dist is less than or equal to attack range i.e 1 always and restricts attack if it is greater.

We also use RED_HORSE &  SHADOWRUNNER Cards to demonstrate the use case +1 -1 Horse related to distance. When these cards are used
specific print statements in the sample output allow 


###  Code Snippet 

```kotlin
fun calculateBaseDistance(from: General, to: General): Int {
        // Circular Distance Approach
        val list = list
        val n = list.size

        val fromIndex = list.indexOf(from)
        val toIndex = list.indexOf(to)

        val clockwise = (toIndex - fromIndex + n) % n
        val counter = (fromIndex - toIndex + n) % n

        return minOf(clockwise, counter)
    }
    fun getDistance(source: General, target: General): Int {
        val baseDistance = calculateBaseDistance(source, target)

        val modifiedDistance = baseDistance
        + target.plusHorse
        - source.minusHorse

        println("Distance from ${source.name} to ${target.name} = $modifiedDistance")

        return if (modifiedDistance < 1) 1 else modifiedDistance
    }
```
### Sample Output
![Sample](Distance_Sample.png)


## 6. Attacking Behaviour : Strategy Pattern
Each general in the game (Lord, Loyalist, Rebel, Spy) follows a specific decision-making logic when selecting a target to attack.
They attack different identities based on their identity which related to the game mission. 


### Explanation

Instead of adding decision-making logic in General class, I implement an abstract Strategy which the General contains. Then I implemented
different concreteStrategies based on the identities.
Rebels prioritize attacking the Lord, while Loyalists focus on protecting the Lord by targeting Rebels.
This separation of behavior ensures that the system remains flexible and easy to extend when introducing
new roles or modifying existing behaviors.

Implementing Strategy Pattern helps in acheiving loose coupling between Generals and their Strategies which helps in maintainability 
of the game logic. 

###  Code Snippet



```kotlin
abstract class Strategy {
    abstract fun whomToAttack(
        self: General,
        generals: List<General>
    ): General?
}
class RebelStrategy() : Strategy() {
    override fun whomToAttack(self: General, generals: List<General>): General? {
        return generals.firstOrNull {
            it !== self && it.identity == Identity.LORD
        }
    }

}
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

class LiuBeiStrategy(private  val owner: General) : LordStrategy() {
    private var state : State = UnhealthyState(owner,this)

    fun setState(newState:State){
        state = newState
    }
    fun updateState(){
        if(owner.currentHP>=2){
            state = HealthyState(owner,this)
        }else{
            state = UnhealthyState(owner,this)
        }
    }
    fun playNextCard(){
        updateState()
        state.playNextCard()
    }
    fun isUnhealthy(): Boolean {
        return owner.currentHP < 2
    }
}

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





```
### UML 
![UML Diagram](Strategy_UML.png)

### Sample
![Sample](Strategy_Sample.png)



## 7. Healthy & UnHealthy  : State Pattern

General's behaviour might change during the execution of the program. The change can depend on health levels. Depending on 
State, General might activate their skills. 

### Explanation

One way to implement is by using multiple if-else statements. For example, until currentHP reaches a certain level, play
in this way and once it reaches below 2 play in another style. 
This means that the code inside General will have multiple if-else for different parts i.e beingAttacked, playTurn, etc 
By using State Pattern, the changes are not constrained within some if-else but rather the changes become state transitions. 

This helps in separation of game-rules from behaviour as part of the project. 


###  Code Snippet

```kotlin
interface State {
    fun playNextCard()
}

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

class HealthyState(
    private val owner:General,
    private val strategy: LiuBeiStrategy
) : State{


    override fun playNextCard() {
        val opponents = GeneralManager.list.filter { it != owner }

        val target = opponents.firstOrNull {
            it.identity == Identity.REBEL
        }
        val attackCard = owner.removeFirstCardOfType(CardType.ATTACK)
        if (attackCard!=null && target != null) {

            println("[Attack Card - State] ${owner.name} spends a card to attack the rebel, ${target.name}.")
            GeneralManager.deck.discard(attackCard)

            if (owner.strategy is LordStrategy) {
                (owner.strategy as LordStrategy).lordUsedAttack()
            }

            target.beingAttacked()

        } else {
            println("${owner.name} doesn't have attack card.")
        }


    }

}

```
### UML
![UML Diagram](State_UML.png)

### Sample
![Sample](State_Sample.png)




