class NonLordFactory(
    private var head: WeiGeneral? = null,
    private val totalPlayers:Int
) : GeneralFactory() {

    private val availableNames =
        mutableSetOf("ZhangFei","GuanYu", "ZhaoYun","DiaoChan","ZhouYu","SimaYi","XuChu")

    private var tail: WeiGeneral? = head


    override fun createRandomGeneral(identity: Identity): General? {

        if (availableNames.isEmpty()) return null

        val name = availableNames.random()
        availableNames.remove(name)

        val strategy = when(identity){
            Identity.REBEL ->RebelStrategy()
            Identity.LOYALIST-> LoyalistsStrategy()
            Identity.SPY -> LoyalistsStrategy()// Place-holder (TEMP)
            else -> throw IllegalArgumentException("Invalid Identity")
        }

        val general = when (name) {
            "ZhangFei" -> ZhangFei(identity,strategy)
            "GuanYu" -> GuanYuAdapter(GuanYu(),identity,strategy)
            "ZhaoYun" -> ZhaoYun(identity,strategy)
            "DiaoChan" -> DiaoChan(identity,strategy)
            "ZhouYu" -> ZhouYu(identity,strategy)
            "SimaYi"-> SimaYi(identity,strategy)
            "XuChu" -> XuChu(identity,strategy)
            else -> throw IllegalArgumentException("Unknown Non-lord")
        }

        if (identity == Identity.SPY) {
            general.strategy = SpyStrategy(general)
        }

        general.currentHP = general.maxHP

        if (general is WeiGeneral) {
            //println("\n added general ${general.name} to Wei")
            addToChain(general)
        }

        return general
    }

    private fun addToChain(newGeneral: WeiGeneral) {
        if (head == null) {
            head = newGeneral
            tail = newGeneral
        } else {
            tail?.nextWei = newGeneral
            tail = newGeneral
        }
    }


    fun getChainHead(): WeiGeneral? = head
}
