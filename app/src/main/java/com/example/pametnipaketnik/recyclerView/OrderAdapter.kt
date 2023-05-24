import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserOrders.Order
import com.example.pametnipaketnik.R

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val boxIdTextView: TextView = view.findViewById(R.id.boxId_text_view)
        val statusTextView: TextView = view.findViewById(R.id.status_text_view)
        val itemsLinearLayout: LinearLayout = view.findViewById(R.id.items_linear_layout)
        val iconImageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.boxIdTextView.text = "Box ID:  ${order.boxId.toString()}             "
        holder.statusTextView.text = "Status:  ${order.status}"

        // Change the icon color based on order status
        when (order.status) {
            "Pending" -> {
                val pendingIconColor = ContextCompat.getColor(holder.itemView.context, R.color.pendingIconColor)
                holder.iconImageView.setColorFilter(pendingIconColor)
            }
            "In Route" -> {
                val inRouteIconColor = ContextCompat.getColor(holder.itemView.context, R.color.inRouteIconColor)
                holder.iconImageView.setColorFilter(inRouteIconColor)
            }
            "Completed" -> {
                val completedIconColor = ContextCompat.getColor(holder.itemView.context, R.color.completedIconColor)
                holder.iconImageView.setColorFilter(completedIconColor)
            }
            else -> {
                val defaultIconColor = ContextCompat.getColor(holder.itemView.context, R.color.defaultIconColor)
                holder.iconImageView.setColorFilter(defaultIconColor)
            }
        }
        println("proba2")
        // Add the items to the LinearLayout
        holder.itemsLinearLayout.removeAllViews()

        val itemCountTextView = TextView(holder.itemView.context)
        val itemCount = order.items?.size ?: 0
        itemCountTextView.text = "$itemCount"
        itemCountTextView.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.black
            )
        )

        val customTypeface = ResourcesCompat.getFont(holder.itemView.context, R.font.exo)
        itemCountTextView.typeface = customTypeface

        // Set a top margin for spacing between count and other views
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 16, 0, 0) // Top margin is 16dp
        itemCountTextView.layoutParams = layoutParams

        holder.itemsLinearLayout.addView(itemCountTextView)
    }

    override fun getItemCount() = orders.size
}
