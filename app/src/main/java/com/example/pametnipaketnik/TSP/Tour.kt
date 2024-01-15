package tsp


class Tour {
    var distance: Double
    var dimension: Int
    var path: MutableList<City>

    constructor(tour: Tour) {
        distance = tour.distance
        dimension = tour.dimension
        path = tour.path.toMutableList()
    }

    constructor(dimension: Int) {
        this.dimension = dimension
        path = MutableList(dimension) { City(0,0.0,0.0) }
        distance = Double.MAX_VALUE
    }

    fun clone(): Tour {
        return Tour(this)
    }

    fun setCity(index: Int, city: City) {
        path[index] = city
        distance = Double.MAX_VALUE
    }
}
