package com.example.pametnipaketnik.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserBoxes.Box
import com.example.pametnipaketnik.R

class BoxAdapter(private val boxes: List<Box>) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val boxId: TextView = itemView.findViewById(R.id.text_box_id)
        val latitude: TextView = itemView.findViewById(R.id.text_latitude)
        val longitude: TextView = itemView.findViewById(R.id.text_longitude)
        // Add more views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.box_item, parent, false)
        return BoxViewHolder(view)
    }

    override fun getItemCount() = boxes.size

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val box = boxes[position]
        holder.boxId.text = "Box ID: ${box.boxId}"
        holder.latitude.text = "Latitude: ${box.latitude}"
        holder.longitude.text = "Longitude: ${box.longitude}"
    }
}
