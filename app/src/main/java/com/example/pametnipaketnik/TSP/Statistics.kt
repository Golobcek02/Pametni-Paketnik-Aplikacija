package tsp

import kotlin.math.pow

class Statistics {
    val dictionary = mutableMapOf<String, MutableList<Double>>()

    fun add(key: String, value: Double) {
        if (dictionary.containsKey(key)) {
            dictionary[key]?.add(value)
        } else {
            dictionary[key] = mutableListOf(value)
        }
    }

    fun calculateAverage(key: String): Double {
        var sum = 0.0
        for (value in dictionary[key]!!) {
            sum += value
        }
        return sum / dictionary[key]!!.size
    }

    fun calculateStandardDeviation(key: String): Double {
        val average = calculateAverage(key)
        var sum = 0.0
        for (value in dictionary[key]!!) {
            sum += (value - average).pow(2.0)
        }
        return (sum / dictionary[key]!!.size).pow(0.5)
    }

    fun returnStatisticsForProblem(key: String): String {
            return "$key min: ${dictionary[key]?.min()} avg: ${calculateAverage(key)}  std: ${calculateStandardDeviation(key)}\n"
    }

    fun printStatistics() {
        for (key in dictionary.keys) {
            println(
                "$key min: ${dictionary[key]?.min()} avg: ${calculateAverage(key)}  std: ${
                    calculateStandardDeviation(
                        key
                    )
                }"
            )
        }
    }
}