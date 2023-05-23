package com.example.pametnipaketnik.ui.home

import OrderAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pametnipaketnik.databinding.FragmentHomeBinding
import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.OrderInterface
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var orderInterface: OrderInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5551/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        orderInterface = retrofit.create(OrderInterface::class.java)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)?.getString("user_id", "")
        println("userId:"+ userId)
        if (userId != null) {
            println("prva")
            lifecycleScope.launch {
                try {
                    println("druga")
                    val response = orderInterface.getUserOrders(userId)
                    println("tretja")
                    println(response)
                    val orders = response

                    // Set up RecyclerView after fetching orders
                    val ordersRecyclerView: RecyclerView = binding.ordersRecyclerView
                    ordersRecyclerView.layoutManager = LinearLayoutManager(context)
                    ordersRecyclerView.adapter = OrderAdapter(orders)

                    orders.forEach {
                        println(it)
                    }

                } catch (e: Exception) {
                    // Handle error
                    println("Error fetching user orders: $e")
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}