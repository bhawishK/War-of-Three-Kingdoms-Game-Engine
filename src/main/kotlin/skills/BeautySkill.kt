package skills
import General
class BeautySkill : Skill {

    override val name = "Beauty Outshining the Moon"

    override fun onTurnStart(owner: General) {

        val card = GeneralManager.deck.draw()
        owner.hand.add(card)

        println("[Beauty] ${owner.name} draws 1 extra card.")
    }
}