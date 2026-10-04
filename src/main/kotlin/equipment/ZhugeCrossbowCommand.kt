package equipment

import General
import cards.Suit
import spells.Command

class ZhugeCrossbowCommand(
    private val owner: General ) : Command {

    override fun execute() {
        println("[ZhugeCrossbow] ${owner.name} can attack any number of times!")
        owner.attackLimit = false
    }
}