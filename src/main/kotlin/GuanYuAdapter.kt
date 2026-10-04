class GuanYuAdapter(
    private val guanYu: GuanYu,
    identity: Identity,
    strategy: Strategy
) : General("Guan Yu", identity, strategy) {

    override val maxHP: Int = guanYu.maximumHP
}
