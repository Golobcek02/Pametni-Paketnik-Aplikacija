package com.example.pametnipaketnik.startup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentStartupPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StartupPage : Fragment() {
    private lateinit var binding: FragmentStartupPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentStartupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginPage.setOnClickListener {
            // Hide the app bar
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()

            // Navigate to the login page
            findNavController().navigate(R.id.login_page)
        }
    }
}
