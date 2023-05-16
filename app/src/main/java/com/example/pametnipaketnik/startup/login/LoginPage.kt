package com.example.pametnipaketnik.startup.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.API.LoginInterface
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentLoginPageBinding
import com.example.pametnipaketnik.databinding.FragmentStartupPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class LoginPage : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var loginInterface: LoginInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5551/")
            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        loginInterface = retrofit.create(LoginInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("banananjiuhuhuhu")
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        binding.returnToHome.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
            navView.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = loginInterface.getData()
            if (response.neke != null) {
                // Data is not null, handle it accordingly
                println(response.neke)
            } else {
                // Data is null, handle the case where no data is returned
                println("No data available")
            }
            withContext(Dispatchers.Main) {
                // Handle the API response on the main thread
                // For example, update UI or navigate to a different fragment
            }
        }

        return binding.root
    }

}