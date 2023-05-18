package com.example.pametnipaketnik.map

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.API.Map.MapInterface
import com.example.pametnipaketnik.API.Register.RegisterInterface
import com.example.pametnipaketnik.API.Register.RegisterRequest
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentHomeBinding
import com.example.pametnipaketnik.databinding.FragmentMapPageBinding
import com.example.pametnipaketnik.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception


class MapPage : Fragment() {
    private var _binding: FragmentMapPageBinding? = null;
    private lateinit var mapInterface: MapInterface;
    private val binding get() = _binding!!;
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5551/")
//            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mapInterface = retrofit.create(MapInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapPageBinding.inflate(inflater, container, false)

        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.VISIBLE
        val sharedPreferences =
            activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("user_id", "")

        val osmConf = Configuration.getInstance()
        osmConf.userAgentValue = requireActivity().packageName
        osmConf.osmdroidBasePath = requireActivity().getExternalFilesDir(null)
        osmConf.osmdroidTileCache = File(osmConf.osmdroidBasePath, "tiles")

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isHorizontalMapRepetitionEnabled = false
        map.isVerticalMapRepetitionEnabled = false

        val mapController: IMapController = map.controller
        mapController.setZoom(7.0)

        /*CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = id?.let { mapInterface.getUserBoxes(it.toInt()) }
                println(response)
            } catch (e: Exception) {
                println("error")
                println(e)
            }
        }*/

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
