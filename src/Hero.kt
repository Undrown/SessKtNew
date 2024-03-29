import java.util.*
import kotlin.concurrent.fixedRateTimer

class Hero(n: String) : Unit(0) {
    val name:String = n
    var huntLvl = 1
    private var xp = 0L
    private var xpMax = 0L
    var freeStats = 0
    var huntService = fixedRateTimer("hunting", period = 1000L){
        hunt(huntLvl)
    }

    constructor():this("Default")

    init {
        recalculate()
    }

    override fun getHit(dmg: Float) {
        hp -= dmg
        if(hp<=0)
            die()
    }

    override fun gainXP(amount: Long) {
        xp+=amount
        if(xp >= xpMax)lvlUp()
        //flying text optionally
    }

    private fun lvlUp(){
        lvl++
        xp -= xpMax
        xpMax = 5*lvl*lvl*lvl+60*lvl*lvl+350*lvl+250L
        freeStats += 5
        //anim
        println("$name lvl up\n")
    }

    override fun die() {
        //
        hitService.cancel()
        regenService.cancel()
        oppos.forEach { oppo ->
            oppo.oppos.removeIf { t -> t.hp <= 0 }
        }
        oppos.clear()
        //position to 0
        hp = 100f
        mp = 1f
        xp = 0
        print("$name died!\n")
    }

    fun save(){
        //saver
    }

    fun load(){
        //loader
    }

    fun hunt(lvl:Int){
        if(oppos.isEmpty() && hp > hpMax/3 && Random().nextInt(100) > 97){
            val oppoLvl = Random().nextInt(4)-2+lvl
            oppos.add(Unit(oppoLvl))
            println("Oppo added: lvl $lvl")
        }
    }

    override fun toString():String{
        return "$name[$hp/$hpMax]"
    }

    override fun toSimpleString(): String {
        return """
Hero: $name
${super.toString()}
        """.trimIndent()
    }
}