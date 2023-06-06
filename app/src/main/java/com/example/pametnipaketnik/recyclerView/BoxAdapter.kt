package com.example.pametnipaketnik.recyclerView

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserBoxes.Box
import com.example.pametnipaketnik.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoxAdapter(private val boxes: List<Box>) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val boxId: TextView = itemView.findViewById(R.id.text_box_id)
        val timeAccessed: TextView = itemView.findViewById(R.id.text_time_accessed)
        val entryType: TextView = itemView.findViewById(R.id.entry_description)
        val icon: ImageView = itemView.findViewById(R.id.entry_icon)
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
        when (box.EntryType) {
            "boxOpening" -> {
                holder.entryType.text = "Box has been opened"
                holder.icon.setImageResource(R.drawable.box_opening)
                holder.icon.setColorFilter(Color.parseColor("#2B3467"), PorterDuff.Mode.SRC_IN)
            }

            "orderAdded" -> {
                holder.entryType.text = "Order was added"
                holder.icon.setImageResource(R.drawable.order_added)
                holder.icon.setColorFilter(Color.parseColor("#579BB1"), PorterDuff.Mode.SRC_IN)
            }

            "orderCompleted" -> {
                holder.entryType.text = "Order is delivered"
                holder.icon.setImageResource(R.drawable.order_complete)
                holder.icon.setColorFilter(Color.parseColor("#579BB1"), PorterDuff.Mode.SRC_IN)
            }

            "oneStopCloser" -> {
                holder.entryType.text = "Order is one step closer"
                holder.icon.setImageResource(R.drawable.one_step_closer)
                holder.icon.setColorFilter(Color.parseColor("#579BB1"), PorterDuff.Mode.SRC_IN)
            }

            "boxRemovedFromOwner" -> {
                holder.entryType.text = "Removed a box"
                holder.icon.setImageResource(R.drawable.removed_box)
                holder.icon.setColorFilter(Color.parseColor("#EB455F"), PorterDuff.Mode.SRC_IN)
            }

            "accessRevoked" -> {
                holder.entryType.text = "Revoked access to user"
                holder.icon.setImageResource(R.drawable.revoke_access)
                holder.icon.setColorFilter(Color.parseColor("#EB455F"), PorterDuff.Mode.SRC_IN)
            }

            "accessAdded" -> {
                holder.entryType.text = "Added access to user"
                holder.icon.setImageResource(R.drawable.addedd_access)
                holder.icon.setColorFilter(Color.parseColor("#EB455F"), PorterDuff.Mode.SRC_IN)
            }

            "boxAdded" -> {
                holder.entryType.text = "Added box to account"
                holder.icon.setImageResource(R.drawable.added_box_to_account)
                holder.icon.setColorFilter(Color.parseColor("#EB455F"), PorterDuff.Mode.SRC_IN)
            }
        }
        // Convert Unix timestamp to human-readable date
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(box.TimeAccessed)
        holder.timeAccessed.text = "Access Time: ${sdf.format(date)}"
    }
}
