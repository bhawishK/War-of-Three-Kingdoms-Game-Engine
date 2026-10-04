class LordFactory : GeneralFactory() {

    private val availableLords = listOf("LiuBei", "CaoCao", "SunQuan")

    override fun createRandomGeneral(identity: Identity): General {
        val name = availableLords.random()
        val general = when (name) {
            "LiuBei" -> {
                val liuBei = LiuBei(Identity.LORD, LordStrategy())
                liuBei.strategy = LiuBeiStrategy(liuBei)
                liuBei.currentHP = liuBei.maxHP + 1
                liuBei
            }
            "CaoCao" -> {
                val caocao =  CaoCao(Identity.LORD,LordStrategy())
                caocao.currentHP = caocao.maxHP + 1
                caocao
            }
            "SunQuan" ->{
                val sunQuan = SunQuan(Identity.LORD,LordStrategy())
                sunQuan.currentHP = sunQuan.maxHP + 1
                sunQuan
            }
            else -> throw IllegalArgumentException("Unknown Lord")
        }
        //general.currentHP = general.maxHP + 1
        return general
    }
}