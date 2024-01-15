package com.example.pametnipaketnik.TSP

class DisplayCity {
    var cityName: String
    var isSelected: Boolean
    var index: Int

    constructor(index: Int, cityName: String, isSelected: Boolean) {
        this.index = index
        this.cityName = cityName
        this.isSelected = isSelected
    }
}