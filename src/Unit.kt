import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow
import kotlin.random.Random

open class Unit(var lvl: Int) {
    //visual numbers
    var hp = 10000f
    var hpMax = 100000f
    var mp = 0f
    var mpMax = 0f
    //internal numbers
    val regenDelay = 100L
    var mpRegen = 0f
    var hpRegen = 0f
    var atkSpeed = 0f
    var atkCooldown = 100L
    var atkMin = 0f
    var atkMax = 0f
    var atkReady:Boolean = false
    //positioning & moving
    var positionX = 0f
    var positionY = 0f
    var speed = 0f
    //links
    val oppos = mutableListOf<Unit>()
    //services
    var regenService = fixedRateTimer("regen", period = regenDelay){
        regenTick()
    }
    var hitService = fixedRateTimer("hit", period = atkCooldown){
        if(oppos.isNotEmpty()){
            hit()
        }
    }


    var statStr = 1.0f
    var statAgi = 1.0f
    var statSta = 1.0f
    var statSpi = 1.0f
    var statInt = 1.0f
    var statWis = 1.0f

    fun add(stat:String){
        when(stat){
            "str"-> statStr++
            "agi"-> statAgi++
            "sta"-> statSta++
            "spi"-> statSpi++
            "int"-> statInt++
            "wis"-> statWis++
            else -> {
                error("Error adding stat: $stat")
            }
        }
    }

    fun randomize(lvl:Int){
        val statnames = listOf("str", "agi", "sta", "spi", "int", "wis")
        for (i in 1..lvl){
            add(statnames[Random.nextInt(6)])
        }
    }

    init {
        if(lvl < 0)
            lvl = 0
        randomize(lvl)
        recalculate()

    }

    open fun regenTick(){
        if(hp > hpMax)
            hp = hpMax
        else if(hp < hpMax)
            hp+=hpRegen
        if(mp > mpMax)
            mp = mpMax
        else if(mp < mpMax)
            mp+=mpRegen
    }

    open fun hit(){
        val dmg = Random.nextFloat()*(atkMax-atkMin) + atkMin
        val oppo = oppos[Random.nextInt(oppos.size)]
        if (this !in oppo.oppos){
            oppo.oppos.add(this)
        }
        oppo.getHit(dmg)
        println("$this hits $oppo with $dmg")
    }

    open fun getHit(dmg:Float){
        hp -= dmg
        if(hp < 0)
            die()
    }

    fun recalculate(){
        atkSpeed = statAgi.pow(1/3) * 0.3f + 1.25f * statAgi.pow(1/4) - 1.3f//hit per sec
        atkCooldown = (1000000L/atkSpeed).toLong()
        atkMin = statStr * 5 + lvl * 2
        atkMax = statStr * 5 + lvl * 2+10
        hpMax = statSta * 10 + lvl * 5 + 100
        hpRegen = (statSta * 0.05f + statSpi * 0.05f + 0.05f)/regenDelay
        mpRegen = (statInt * 0.2f + statWis * 0.05f + 0.05f)/regenDelay
        speed = 40f//TODO:make speed changeable by equipment
    }

    open fun die(){
        hitService.cancel()
        regenService.cancel()
        val c = oppos.size
        oppos.forEach { oppo ->
            oppo.gainXP(this.lvl*100L / c)//TODO: make calculations for xp amount
        }
        oppos.forEach { oppo ->
            oppo.oppos.removeIf{u -> u.hp < 0}
        }
        oppos.clear()
        println("DIED")
    }

    open fun gainXP(amount:Long){
        //for heroes only
    }

    override fun toString():String{
        return "Unit[$hp/$hpMax]"
    }

    open fun toSimpleString(): String {
        return """
Unit:${super.toString()}
lvl:$lvl
stats:
str:$statStr
agi:$statAgi
sta:$statSta
spi:$statSpi
int:$statInt
wis:$statWis

        """.trimIndent()
    }
}