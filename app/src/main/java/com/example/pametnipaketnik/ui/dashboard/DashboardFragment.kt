package com.example.pametnipaketnik.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pametnipaketnik.API.GetUserBoxes.BoxInterface
import com.example.pametnipaketnik.databinding.FragmentDashboardBinding
import com.example.pametnipaketnik.recyclerView.BoxAdapter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var boxInterface: BoxInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ppbackend.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        boxInterface = retrofit.create(BoxInterface::class.java)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)?.getString("user_id", "")
        if (userId != null) {
            println("ne dela")
            lifecycleScope.launch {
                try {
                    println("proba")
                    val response = boxInterface.getUserBoxes(userId)
                    println("proba1")
                    println(response)
                    if (response.isSuccessful) { // Check if the HTTP request was successful
                        val boxes = response.body() // Extract the list of boxes from the response
                        if (boxes != null) {
                            println("proba2")
                            // Set up RecyclerView after fetching boxes
                            val boxRecyclerView: RecyclerView = binding.boxRecyclerView
                            boxRecyclerView.layoutManager = LinearLayoutManager(context)
                            boxRecyclerView.adapter = BoxAdapter(boxes)
                        } else {
                            println("Body of the response is null")
                        }
                    } else {
                        println("Unsuccessful response: ${response.code()}")
                    }

                } catch (e: Exception) {
                    // Handle error
                    println("Error fetching boxes: $e")
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}