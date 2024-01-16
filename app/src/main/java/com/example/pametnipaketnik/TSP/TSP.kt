package tsp

import android.util.Log
import okhttp3.OkHttpClient
import org.json.JSONObject
import parsing.Parser
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt
import com.example.pametnipaketnik.TSP.RandomUtils

class TSP(path: String, var maxEvaluations: Int) {

    enum class DistanceType {
        EUCLIDEAN,
        WEIGHTED
    }
    lateinit var name: String;
    lateinit var start: City
    var cities: MutableList<City> = mutableListOf()
    var numberOfCities = 0
    lateinit var weights: MutableList<DoubleArray>
    var distanceType = DistanceType.EUCLIDEAN
    var numberOfEvaluations = 0

    init {
        RandomUtils.setSeedFromTime()
        if (!path.contains("android") &&((!path.contains("distance") && path.contains("time")) || (!path.contains("time") && path.contains("distance")))) {
            loadData(path)
        } else if(!path.contains("android")) {
            name = path
            loader(path)
            distanceType = DistanceType.WEIGHTED
        }
        numberOfEvaluations = 0
    }

    constructor(path: String, maxEval: Int, c: MutableList<City>, w: List<String>) : this(path, maxEval) {
        name = path
        androidLoader(path, c, w)
        distanceType = DistanceType.WEIGHTED
    }


    fun getLocation(jsonString: String): Pair<Double, Double> {
        val jsonObject = JSONObject(jsonString)

        val res = jsonObject.getJSONArray("features")
        val distance =
            res.getJSONObject(0).getJSONObject("properties")["distance"].toString().toDouble()
        val time = res.getJSONObject(0).getJSONObject("properties")["time"].toString().toDouble()

        return Pair(distance, time)
    }


    fun androidLoader(path: String, c: MutableList<City>, w: List<String>){
        val client = OkHttpClient()

        for(i in c){
            cities.add(i.clone())
        }

        numberOfCities = cities.size
        start = cities[0].clone()
        weights = MutableList(cities[cities.size-1].index) { DoubleArray(cities[cities.size-1].index) }

        if (path.contains("preloaded")) {
            Log.i("preloaded", cities[cities.size-1].index.toString())
            for (i in 0 until cities[cities.size-1].index){
                val line = w[i].split(" ")
                for (j in 0 until cities[cities.size-1].index){
                    if (i != j) {
                        weights[i][j] = line[j].toDouble()
                    }
                }
            }

        } else {
            for (i in 0 until cities.size) {
                for (j in 0 until cities.size) {
                    if (i != j) {
                        val request: okhttp3.Request = okhttp3.Request.Builder()
                            .url("https://api.geoapify.com/v1/routing?waypoints=${cities[i].x},${cities[i].y}|${cities[j].x},${cities[j].y}&mode=medium_truck&apiKey=635b84cbf55241c6b792a66cd02745a9")
                            .build()

                        val response: okhttp3.Response = client.newCall(request).execute()
                        response.body?.let {
                            val tmp = getLocation(it.string())
                            if (path.contains("distance"))
                                weights[i][j] = tmp.first
                            else
                                weights[i][j] = tmp.second
                        }
                    }
                }
            }
        }
    }


    fun loader(path: String) {
        val client = OkHttpClient()
        if (cities.size == 0) {
            val parser = Parser("raw/data.dat")
            val lines = parser.parse()

            for (i in lines.indices step 3) {
                cities.add(City(1 + (i / 3), lines[i].toDouble(), lines[i + 1].toDouble()))
            }
        }

        numberOfCities = cities.size
        start = cities[0].clone()
        weights = MutableList(numberOfCities) { DoubleArray(numberOfCities) }

        if (path.contains("preloaded")) {
            val file = File(path)
            val lines = file.readLines()
            for (i in 0 until cities.size) {
                val line = lines[i].split(" ")
                for (j in 0 until cities.size) {
                    if (i != j) {
                        weights[i][j] = line[j].toDouble()
                    }
                }
            }

        } else {
            for (i in 0 until cities.size) {
                for (j in 0 until cities.size) {
                    if (i != j) {
                        val request: okhttp3.Request = okhttp3.Request.Builder()
                            .url("https://api.geoapify.com/v1/routing?waypoints=${cities[i].x},${cities[i].y}|${cities[j].x},${cities[j].y}&mode=medium_truck&apiKey=635b84cbf55241c6b792a66cd02745a9")
                            .build()

                        val response: okhttp3.Response = client.newCall(request).execute()
                        response.body?.let {
                            val tmp = getLocation(it.string())
                            if (path.contains("distance"))
                                weights[i][j] = tmp.first
                            else
                                weights[i][j] = tmp.second
                        }
                    }
                }
            }
        }

    }

    fun evaluate(tour: Tour) {
        var distance = 0.0
        distance += calculateDistance(start, tour.path[0])
        for (index in 0 until numberOfCities) {
            distance += if (index + 1 < numberOfCities) calculateDistance(
                tour.path[index],
                tour.path[index + 1]
            ) else calculateDistance(tour.path[index], start)
        }
        tour.distance = distance
        numberOfEvaluations++
    }

    private fun calculateEucledian(from: City, to: City): Double {
        return sqrt(
            (from.x - to.x).pow(2.0) + (from.y - to.y).pow(2.0)
        )
    }

    private fun calculateWeighted(from: City, to: City): Double {
        return weights[from.index - 1][to.index - 1]
    }

    private fun calculateDistance(from: City, to: City): Double {
        return when (distanceType) {
            DistanceType.EUCLIDEAN -> calculateEucledian(from, to)
            DistanceType.WEIGHTED -> calculateWeighted(from, to)
            else -> Double.MAX_VALUE
        }
    }

    fun generateTour(): Tour {
        val tour = Tour(numberOfCities)
        for (i in 0 until numberOfCities) {
            tour.setCity(i, cities[i])
        }
        tour.path.shuffle()
        return tour
    }

    private fun loadData(path: String) {
        val file = File(path)
        val lines = file.readLines()
        var values = lines[0].replace(" ", "").split(":")
        name = values[1]

        var index = lines.indexOfFirst { it.contains("DIMENSION") }
        values = lines[index].replace(" ", "").split(":")
        numberOfCities = values[1].toInt()

        index = lines.indexOfFirst { it.contains("EDGE_WEIGHT_TYPE") }
        values = lines[index].replace(" ", "").split(":")

        if (values[1] == "EXPLICIT") {
            index = lines.indexOfFirst { it.contains("EDGE_WEIGHT_SECTION") }
            weights = readEXPLICIT(lines, index + 1)
        } else if (values[1] == "EUC_2D") {
            index = lines.indexOfFirst { it.contains("NODE_COORD_SECTION") }
            weights = readEUC_2D(lines, index + 1)
        }

        start = cities[0].clone()
    }

    private fun readEXPLICIT(lines: List<String>, index: Int): MutableList<DoubleArray> {
        var connections = MutableList(numberOfCities) { DoubleArray(numberOfCities) }
        for (i in 0 until numberOfCities) {
            val line = lines[index + i]
            var values = line.split(" ")
            values = values.filterNot { it.isEmpty() }
            for (j in 0 until numberOfCities) {

                connections[i][j] = values[j].toDouble()
            }
        }

        var index = lines.indexOfFirst { it.contains("DISPLAY_DATA_SECTION") }
        index++
        for (i in 0 until numberOfCities) {
            val line = lines[index + i]
            var values = line.split(" ")
            values = values.filterNot { it.isEmpty() }
            cities.add(City(values[0].toInt(), values[1].toDouble(), values[2].toDouble()))
        }
        distanceType = DistanceType.WEIGHTED
        return connections
    }

    private fun readEUC_2D(lines: List<String>, index: Int): MutableList<DoubleArray> {
        for (i in 0 until numberOfCities) {
            val line = lines[index + i]
            var values = line.split(" ")
            values = values.filterNot { it.isEmpty() }
            val city = City(values[0].toInt(), values[1].toDouble(), values[2].toDouble())
            cities.add(city)
        }

        var connections = MutableList(numberOfCities) { DoubleArray(numberOfCities) }
        for (i in 0 until numberOfCities) {
            for (j in 0 until numberOfCities) {
                connections[i][j] = calculateEucledian(cities[i], cities[j]).toDouble()
            }
        }
        distanceType = DistanceType.WEIGHTED
        return connections
    }
}