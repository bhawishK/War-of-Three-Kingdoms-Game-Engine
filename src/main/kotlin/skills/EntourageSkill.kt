package skills
import General
import WeiGeneral
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