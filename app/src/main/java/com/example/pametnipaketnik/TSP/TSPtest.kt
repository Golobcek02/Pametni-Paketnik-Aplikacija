package tsp

import com.example.pametnipaketnik.TSP.RandomUtils
import java.io.File



object TSPTest {
    @JvmStatic
    fun main(args: Array<String>) {
        RandomUtils.setSeedFromTime() // nastavi novo seme ob vsakem zagonu main metode (vsak zagon bo drugačen)
        var problems = listOf(
            Pair("src/main/resources/bays29.tsp", 1000),
            Pair("src/main/resources/eil101.tsp", 1000),
            Pair("src/main/resources/a280.tsp", 1000),
            Pair("src/main/resources/pr1002.tsp", 1000),
            Pair("src/main/resources/dca1389.tsp", 1000)
        )
        var stats = Statistics()
        var file = File("fes_1000.txt")

        for (i in 0..3) {
            when (i) {
                1 -> {
                    problems = listOf(
                        Pair("src/main/resources/bays29.tsp", 10000),
                        Pair("src/main/resources/eil101.tsp", 10000),
                        Pair("src/main/resources/a280.tsp", 10000),
                        Pair("src/main/resources/pr1002.tsp", 10000),
                        Pair("src/main/resources/dca1389.tsp", 10000)
                    )
                    stats = Statistics()
                    file = File("fes_10000.txt")
                }
                2 -> {
                    problems = listOf(
                        Pair("src/main/resources/bays29.tsp", 100000),
                        Pair("src/main/resources/eil101.tsp", 100000),
                        Pair("src/main/resources/a280.tsp", 100000),
                        Pair("src/main/resources/pr1002.tsp", 100000),
                        Pair("src/main/resources/dca1389.tsp", 100000)
                    )
                    stats = Statistics()
                    file = File("fes_100000.txt")
                }
                3 -> {
                    problems = listOf(
                        Pair("src/main/resources/bays29.tsp", 1000000),
                        Pair("src/main/resources/eil101.tsp", 1000000),
                        Pair("src/main/resources/a280.tsp", 1000000),
                        Pair("src/main/resources/pr1002.tsp", 1000000),
                        Pair("src/main/resources/dca1389.tsp", 1000000)
                    )
                    stats = Statistics()
                    file = File("fes_1000000.txt")
                }
            }

            for (problem in problems) {
                println(problem.first)
                for (i in 0..29) {
                    val tsp= TSP(problem.first, problem.second)
                    val ga = GA(100, 0.8, 0.1)
                    val solution = ga.execute(tsp)
                    println("${tsp.name} ${solution.distance}")
                    stats.add(problem.first, solution.distance)
                }
                println(stats.returnStatisticsForProblem(problem.first))
                file.appendText(stats.returnStatisticsForProblem(problem.first))
            }
        }
    }
}