package skills
import General
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