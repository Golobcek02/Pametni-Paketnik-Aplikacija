import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserOrders.Order
import com.example.pametnipaketnik.R

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idTextView: TextView = view.findViewById(R.id.id_text_view)
        val boxIdTextView: TextView = view.findViewById(R.id.boxId_text_view)
        // Add more views based on your item_order layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.idTextView.text = order.id
        holder.boxIdTextView.text = order.boxId.toString()
        // Bind more data to views
    }

    override fun getItemCount() = orders.size
}
