package skills
import General
interface Skill {
    val name : String
    fun onTurnStart(owner:General){}
    fun onBeingAttacked(owner:General,attacker:General?){}
    fun onAfterDodge(owner:General){}
    fun onAttack(owner:General, target: General){}
}