abstract class Strategy {
    abstract fun whomToAttack(
        self: General,
        generals: List<General>
    ): General?
}