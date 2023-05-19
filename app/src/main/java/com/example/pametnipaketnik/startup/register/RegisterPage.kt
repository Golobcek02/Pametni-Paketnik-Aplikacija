package com.example.pametnipaketnik.startup.register

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.API.Register.RegisterInterface
import com.example.pametnipaketnik.API.Register.RegisterRequest
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentRegisterPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RegisterPage : Fragment() {
    private lateinit var binding: FragmentRegisterPageBinding
    private lateinit var registerInterface: RegisterInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5551/")
//            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        registerInterface = retrofit.create(RegisterInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        binding = FragmentRegisterPageBinding.inflate(inflater, container, false)
        binding.buttonRegister.setOnClickListener {
            binding.textviewPassword.setTextColor(Color.BLACK)
            binding.textviewUsername.setTextColor(Color.BLACK)
            binding.textviewEmail.setTextColor(Color.BLACK)
            binding.textviewName.setTextColor(Color.BLACK)
            binding.textviewSurname.setTextColor(Color.BLACK)
            if (binding.passwordInput.text.isNullOrEmpty() || binding.usernameInput.text.isNullOrEmpty() || binding.nameInput.text.isNullOrEmpty() || binding.emailInput.text.isNullOrEmpty() || binding.surnameInput.text.isNullOrEmpty()) {
                binding.textviewPassword.setTextColor(Color.RED)
                binding.textviewUsername.setTextColor(Color.RED)
                binding.textviewEmail.setTextColor(Color.RED)
                binding.textviewName.setTextColor(Color.RED)
                binding.textviewSurname.setTextColor(Color.RED)
                binding.textviewInputError.setText("You have to enter all listed credentials")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = RegisterRequest(
                            binding.nameInput.text.toString(),
                            binding.surnameInput.text.toString(),
                            binding.usernameInput.text.toString(),
                            binding.emailInput.text.toString(),
                            binding.passwordInput.text.toString()
                        )
                        val response = registerInterface.register(request)
                        if (response.res == "Proceede") {
                            withContext(Dispatchers.Main) {
                                findNavController().navigate(R.id.register_createFaceID)
                            }
                        } else {
                            println("No data available")
                        }
                    } catch (e: Exception) {
                        println(e)
                        requireActivity().runOnUiThread {
                            binding.textviewInputError.setText("User already exists")
                            binding.usernameInput.setText("")
                            binding.passwordInput.setText("")
                            binding.nameInput.setText("")
                            binding.surnameInput.setText("")
                            binding.emailInput.setText("")
                        }

                    }
                }
            }
        }

        return binding.root
    }

}