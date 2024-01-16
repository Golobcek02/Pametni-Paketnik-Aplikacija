package tsp

import com.example.pametnipaketnik.TSP.RandomUtils

class GA(
    var popSize: Int, //crossover probability
    var cr: Double, //mutation probability
    var pm: Double
) {
    var population: MutableList<Tour> = mutableListOf()
    var offspring: MutableList<Tour> = mutableListOf()

    fun execute(problem: TSP): Tour {
        var best = Tour(problem.numberOfCities)
        for (i in 0 until popSize) {
            val newTour = problem.generateTour()
            problem.evaluate(newTour)
            population.add(newTour)
            if (newTour.distance < best.distance) {
                best = newTour.clone()
            }
        }
        while (problem.numberOfEvaluations < problem.maxEvaluations) {
            offspring.add(getBest().clone())
            while (offspring.size < popSize) {
                val parent1 = tournamentSelection()
                val parent2 = tournamentSelection()
                if (RandomUtils.nextDouble() < cr) {
                    val children = pmx(parent1, parent2)
                    offspring.add(children[0])
                    if (offspring.size < popSize) offspring.add(children[1])
                } else {
                    offspring.add(parent1.clone())
                    if (offspring.size < popSize) offspring.add(parent2.clone())
                }
            }

            for (off in offspring) {
                var offsp = off
                if (RandomUtils.nextDouble() < pm) {
                    offsp=swapMutation(offsp).clone()
                }

                problem.evaluate(offsp)
                if (offsp.distance < best.distance) {
                    best = offsp.clone()
                }
            }

            population = offspring.toMutableList()
            offspring.clear()
        }
        return best
    }

    private fun swapMutation(off: Tour): Tour {
        val index1 = RandomUtils.nextInt(off.dimension)
        var index2 = RandomUtils.nextInt(off.dimension)

        if (index1 == index2) {
            if (index2 == off.dimension - 1) {
                index2 = 0
            } else {
                index2 += 1
            }
        }

        val city1 = off.path[index1].clone()
        val city2 = off.path[index2].clone()

        off.setCity(index1, city2)
        off.setCity(index2, city1)
        return off
    }

    private fun getBest(): Tour {
        var best: Tour = population[0]
        for (tour in population) {
            if (tour.distance < best.distance) {
                best = tour.clone()
            }
        }
        return best
    }


    private fun pmx(parent1: Tour, parent2: Tour): List<Tour> {
        val size = parent1.path.size
        val index1 = RandomUtils.nextInt(size / 2)
        val index2 = RandomUtils.nextInt(size / 2 + 1, size)

        val child11 = Tour(size)
        val child22 = Tour(size)

        for (i in index1 until index2) {
            child22.path[i] = parent1.path[i].clone()
            child11.path[i] = parent2.path[i].clone()
        }

        val mapping1to2 = (index1 until index2).associate { parent1.path[it] to parent2.path[it] }
        val mapping2to1 = (index1 until index2).associate { parent2.path[it] to parent1.path[it] }

        for (i in 0 until size) {
            if (i < index1 || i >= index2) {
                var currentCity = parent1.path[i].clone()

                while (currentCity in mapping2to1) {
                    currentCity = mapping2to1[currentCity]!!.clone()
                }

                if (currentCity !in child22.path) {
                    child22.path[i] = currentCity.clone()
                }
            }
        }

        for (i in 0 until size) {
            if (i < index1 || i >= index2) {
                var currentCity = parent2.path[i].clone()

                while (currentCity in mapping1to2) {
                    currentCity = mapping1to2[currentCity]!!.clone()
                }

                if (currentCity !in child11.path) {
                    child11.path[i] = currentCity.clone()
                }
            }
        }

        return listOf(child22, child11)
    }


    private fun tournamentSelection(): Tour {
        val index1 = RandomUtils.nextInt(population.size)
        var index2 = RandomUtils.nextInt(population.size)
        if (index1 == index2) {
            if (index2 == population.size - 1) {
                index2 = 0
            } else {
                index2 += 1
            }
        }

        val tour1 = population[index1]
        val tour2 = population[index2]

        return if (tour1.distance < tour2.distance) {
            tour1.clone()
        } else {
            tour2.clone()
        }
    }
}

