import kotlin.math.roundToInt
import kotlin.random.Random

class Zone(val grid:Int){
    val heroes = listOf<Hero>()
    val units = listOf<Unit>()
    val cells = List(grid) { i ->
        List(grid) { j->Cell(i, j)}
    }
    var areas:Int = grid/15
    val border = 3
    val areaMin = 25
    //for GLRenderer
    var camPositionX:Float = 0.0f
    var camPositionY:Float = 0.0f
    var camPositionZ:Float = 15.0f
    var positionX:Float = 0.0f
    var positionY:Float = 0.0f
    var positionZ:Float = 1.0f

    init {
        makeBorder()
        makeAreas()
        markAreas()
        removeSmallAreas()
        markAreas()
    }

    private fun makeBorder(){
        for(i in 0 until grid)
            for (j in 0 until Random.nextInt(border)+1)
                cells[i][j].isPassable = false
        for(i in 0 until grid)
            for (j in 0 until Random.nextInt(border)+1)
                cells[j][i].isPassable = false
        for(i in 0 until grid)
            for (j in grid-1 downTo grid-Random.nextInt(border)-1)
                cells[i][j].isPassable = false
        for(i in 0 until grid)
            for (j in grid-1 downTo grid-Random.nextInt(border)-1)
                cells[j][i].isPassable = false
    }

    private fun makeAreas(){
        var count = areas
        while(count-1 > 0){
            var axis = Random.nextFloat().roundToInt() // can be 0 or 1
            val start = Random.nextInt(1, grid-1)
            var i = if (axis != 0) start else 1
            var j = if (axis != 0) 1 else start
            axis = if (axis == 0) -1 else 1
            var di:Int
            var dj:Int
            while(i in 1 until grid && j in 1 until grid) {
                cells[i][j].isPassable = false
                if(Random.nextInt(0, 100)<50-axis*30)di = 1
                else if(Random.nextInt(0, 100)<50)di = -1
                else di = 0
                if(Random.nextInt(0, 100)<50+axis*30)dj = 1
                else if(Random.nextInt(0, 100)<50)dj = -1
                else dj = 0
                i+=di
                j+=dj
            }
            count --
        }
    }

    private fun markAreas(){
        //nullify cells area
        cells.forEach {
            it.forEach {cell -> cell.area = -1 }
        }
        var id = 0//zId
        for (i in 0 until grid)
            for(j in 0 until grid)
                if(cells[i][j].area < 0 && cells[i][j].isPassable) {
                    id++
                    mark(i, j, id)
                }
        areas = id
    }

    private fun mark(i:Int, j:Int, id:Int){
        //if(cells[i][j].area > 0)return
        cells[i][j].area = id
        if(i in 1 until grid-1 && j in 1 until grid-1){
            if(cells[i-1][j].isPassable && cells[i-1][j].area<0)mark(i-1, j, id)
            if(cells[i][j-1].isPassable && cells[i][j-1].area<0)mark(i, j-1, id)
            if(cells[i+1][j].isPassable && cells[i+1][j].area<0)mark(i+1, j, id)
            if(cells[i][j+1].isPassable && cells[i][j+1].area<0)mark(i, j+1, id)
        }
    }

    private fun removeSmallAreas(){
        while(areas>0){
            var count = 0
            cells.forEach {
                it.forEach { cell ->
                    if(cell.area == areas)
                        count ++

                }
            }
            if(count <= areaMin)
                removeArea(areas)
            areas--
        }
    }

    private fun removeArea(id:Int){
        cells.forEach {
            it.forEach { cell ->
                if(cell.area == id){
                    cell.area = -1
                    cell.isPassable = false
                }
            }
        }
    }

    fun showAreas(){
        for (i in 0 until grid) {
            for (j in 0 until grid)
                print(cells[i][j].area).also { print(" ") }
            print("\n")
        }
    }

    class Cell(val i:Int, val j:Int){
        val size = 10f//10x10 coords
        val x:Float = i*10f
        val y:Float = j*10f
        val z:Float = 0f
        var isPassable:Boolean = true
        var area = -1//uninitialized

        override fun toString(): String {
            return if (isPassable) "." else ";"
        }

    }

    override fun toString(): String {
        var str = ""
        for (i in 0 until grid) {
            for (j in 0 until grid)
                str += cells[i][j]
            str += "\n"
        }
        return str
    }
}