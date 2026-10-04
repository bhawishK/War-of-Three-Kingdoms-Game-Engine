import skills.BeautySkill
class ZhangFei(
    identity: Identity,
    strategy: Strategy
) : General("Zhang Fei", identity, strategy) {

    override val maxHP: Int = 4
}

class ZhaoYun(
    identity: Identity,
    strategy: Strategy
) : General("Zhao Yun", identity, strategy) {

    override val maxHP: Int = 4
}


// Existing class
class GuanYu {
    val maximumHP = 4
}

// Your Face You Fate
class ZhouYu(identity: Identity,strategy: Strategy) :General("Zhou Yu",identity,strategy){
    override val maxHP: Int = 3

    override fun drawPhase() {
        repeat(3) {
            hand.add(GeneralManager.deck.draw())
        }
        println("[Heroism] $name draws 3 cards and now has ${hand.size} card(s).")
    }



}
class DiaoChan(identity: Identity, strategy: Strategy)
    : General("Diao Chan", identity, strategy) {

    override val maxHP = 3

    init {
        skills.add(BeautySkill())
    }
}

class SimaYi (identity: Identity,strategy: Strategy) : WeiGeneral("Sima Yi",identity,strategy) {
    override val maxHP: Int = 4
}

class XuChu(identity: Identity,strategy: Strategy) : WeiGeneral("Xu Chu",identity,strategy) {
    override val maxHP: Int = 4
}
