package com.example.pametnipaketnik.recyclerView

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserBoxes.Box
import com.example.pametnipaketnik.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BoxAdapter(private val boxes: List<Box>) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val boxId: TextView = itemView.findViewById(R.id.text_box_id)
        val timeAccessed: TextView = itemView.findViewById(R.id.text_time_accessed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.box_item, parent, false)
        return BoxViewHolder(view)
    }

    override fun getItemCount() = boxes.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val box = boxes[position]
        holder.boxId.text = "Box ID: ${box.boxId}"
        val instant = Instant.ofEpochSecond(box.timeaccessed.toLong())
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        holder.timeAccessed.text = "Access Time: ${localDateTime}"
    }
}
