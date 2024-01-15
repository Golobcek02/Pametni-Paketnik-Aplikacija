package tsp


class City{
    var index = 0
    var x = 0.0
    var y = 0.0

    constructor(index: Int, x: Double, y: Double) {
        this.index = index
        this.x = x
        this.y = y
    }

    fun clone(): City {
        return City(index, x, y)
    }
}
