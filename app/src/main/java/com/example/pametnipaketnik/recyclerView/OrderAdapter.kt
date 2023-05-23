import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserOrders.Order
import com.example.pametnipaketnik.R

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val boxIdTextView: TextView = view.findViewById(R.id.boxId_text_view)
        val statusTextView: TextView = view.findViewById(R.id.status_text_view)
        val itemsLinearLayout: LinearLayout = view.findViewById(R.id.items_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.boxIdTextView.text ="Box ID:  ${order.boxId.toString()}"
        holder.statusTextView.text ="Status:  ${order.status}"
        // Add the items to the LinearLayout
        holder.itemsLinearLayout.removeAllViews() // Clear any old views
        order.items?.forEach { item ->
            val itemTextView = TextView(holder.itemView.context)
            itemTextView.text = item

            // Set a top margin for spacing between items
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 8, 0, 0) // Top margin is 8dp
            itemTextView.layoutParams = layoutParams

            holder.itemsLinearLayout.addView(itemTextView)
        }
    }

    override fun getItemCount() = orders.size
}
