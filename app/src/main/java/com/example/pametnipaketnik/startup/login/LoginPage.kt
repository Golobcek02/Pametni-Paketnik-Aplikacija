package com.example.pametnipaketnik.startup.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.API.Login.LoginInterface
import com.example.pametnipaketnik.API.Login.LoginRequest
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentLoginPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class LoginPage : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var loginInterface: LoginInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ppbackend.azurewebsites.net/")
//            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        loginInterface = retrofit.create(LoginInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        binding.buttonLogin.setOnClickListener {
            binding.textviewPassword.setTextColor(Color.BLACK)
            binding.textviewUsername.setTextColor(Color.BLACK)
            if (binding.passwordInput.text.isNullOrEmpty() && binding.usernameInput.text.isNullOrEmpty()) {
                binding.textviewPassword.setTextColor(Color.RED)
                binding.textviewUsername.setTextColor(Color.RED)
                binding.textviewInputError.setText("You have to enter a Username and Password")
            } else if (binding.passwordInput.text.isNullOrEmpty()) {
                binding.textviewPassword.setTextColor(Color.RED)
                binding.textviewInputError.setText("You have to enter a Password")
            } else if (binding.usernameInput.text.isNullOrEmpty()) {
                binding.textviewUsername.setTextColor(Color.RED)
                binding.textviewInputError.setText("You have to enter a Username")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = LoginRequest(
                            binding.usernameInput.text.toString(),
                            binding.passwordInput.text.toString()
                        )
                        val response = loginInterface.login(request)
                        if (response.Username != null) {
                            println(response.Username)
                            println(response.ID)
                            val sharedPreferences =
                                activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            sharedPreferences?.edit()?.apply {
                                putString("username", response.Username)
                                putString("user_id", response.ID)
                                apply()
                            }
                            withContext(Dispatchers.Main) {
                                findNavController().navigate(R.id.navigation_home)
                                navView.visibility = View.VISIBLE
                            }
                        } else {
                            // Data is null, handle the case where no data is returned
                            println("No data available")
                        }
                    } catch (e: Exception) {
                        println("error")
                        requireActivity().runOnUiThread {
                            binding.textviewInputError.setText("Username or Password incorrect")
                            binding.usernameInput.setText("")
                            binding.passwordInput.setText("")
                        }

                    }
                }
            }
        }

        return binding.root
    }

}