
class LiuBeiStrategy(private  val owner: General) : LordStrategy() {
    private var state : State = UnhealthyState(owner,this)

    fun setState(newState:State){
        state = newState
    }
    fun updateState(){
        if(owner.currentHP>=2){
            state = HealthyState(owner,this)
        }else{
            state = UnhealthyState(owner,this)
        }
    }
    fun playNextCard(){
        updateState()
        state.playNextCard()
    }
    fun isUnhealthy(): Boolean {
        return owner.currentHP < 2
    }
}