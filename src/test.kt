fun main() {
    val name = "Sin"
    val hero = Hero(name)
    val unit = Unit(0)
    hero.oppos.add(unit)
    unit.oppos.add(hero)
    println("added oppo")
}