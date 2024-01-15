package com.example.pametnipaketnik.recyclerView

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserOrders.Order
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.TSP.DisplayCity
import tsp.City

class CityAdapter(private val cities: List<DisplayCity>) :
    RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityCheck: CheckBox = view.findViewById(R.id.cityCheck)
        val itemsLinearLayout: LinearLayout = view.findViewById(R.id.cities_linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.cityCheck.text = city.cityName
        holder.cityCheck.isChecked = city.isSelected

        holder.cityCheck.setOnCheckedChangeListener { _, isChecked ->
            city.isSelected = isChecked
            Log.i("CityAdapter", "City ${city.cityName} is selected: ${city.isSelected}")
        }
    }

    fun getSelectedCities(): List<DisplayCity> {
        return cities.filter { !it.isSelected }
    }


    override fun getItemCount() = cities.size
}
