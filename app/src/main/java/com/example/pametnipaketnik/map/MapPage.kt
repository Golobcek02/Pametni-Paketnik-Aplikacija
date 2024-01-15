package com.example.pametnipaketnik.map

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pametnipaketnik.API.Map.MapInterface
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.TSP.DisplayCity
import com.example.pametnipaketnik.databinding.FragmentMapPageBinding
import com.example.pametnipaketnik.recyclerView.CityAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import parsing.Parser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tsp.City
import tsp.GA
import tsp.TSP
import tsp.Tour


class MapPage : Fragment() {
    private var _binding: FragmentMapPageBinding? = null;
    private lateinit var mapInterface: MapInterface;
    private val binding get() = _binding!!;
    private lateinit var map: MapView
    private lateinit var addBtn: Button
    private lateinit var toggle: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ppbackend.azurewebsites.net/")
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

        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view);
        navView.visibility = View.VISIBLE;
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        val id = sharedPreferences?.getString("user_id", "");

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.isHorizontalMapRepetitionEnabled = false;
        map.isVerticalMapRepetitionEnabled = false;

        val mapController: IMapController = map.controller;
        mapController.setZoom(7.0);
        mapController.setCenter(GeoPoint(46.562511, 15.658693))
        val geocoder = Geocoder(requireContext());

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = mapInterface.getUserBoxes(id.toString())
                val boxes = response.allBoxes

                for (x in boxes) {
                    val circle = Polygon(map)
                    val points = Polygon.pointsAsCircle(GeoPoint(x.Latitude, x.Longitude), 5.0);
                    circle.points = points;
                    circle.fillColor = 0x30111111;
                    circle.strokeColor = 0xFF111111.toInt();
                    circle.strokeWidth = 2.0f;
                    circle.title = x.BoxId.toString();
                    map.overlays.add(circle);
                    map.minZoomLevel = 2.0
                    map.maxZoomLevel = 20.0

                    map.addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            val zoomLevel = map.zoomLevelDouble
                            val metersPerPixel =
                                (156543.03392 * Math.cos(x.Latitude * Math.PI / 180) / Math.pow(
                                    2.0,
                                    zoomLevel
                                )) // Calculate the meters per pixel based on the zoom level and latitude
                            val newRadius =
                                (metersPerPixel * (40.0+map.zoomLevelDouble))-(20-map.zoomLevelDouble)
                            val updatedPoints =
                                Polygon.pointsAsCircle(GeoPoint(x.Latitude, x.Longitude), newRadius)
                            circle.points = updatedPoints
                            map.invalidate()
                            return true
                        }

                        override fun onZoom(event: ZoomEvent?): Boolean {
                            val zoomLevel = map.zoomLevelDouble
                            val metersPerPixel =
                                (156543.03392 * Math.cos(x.Latitude * Math.PI / 180) / Math.pow(
                                    2.0,
                                    zoomLevel
                                )) // Calculate the meters per pixel based on the zoom level and latitude
                            val newRadius =
                                (metersPerPixel * (40.0+map.zoomLevelDouble))-(20-map.zoomLevelDouble)
                            val updatedPoints =
                                Polygon.pointsAsCircle(GeoPoint(x.Latitude, x.Longitude), newRadius)
                            circle.points = updatedPoints
                            map.invalidate()
                            return true
                        }
                    })

                }
            } catch (e: Exception) {
                println("error")
                println(e)
            }
        }

        val recycler = binding.citySelector
        recycler.layoutManager = LinearLayoutManager(context)
        var lines = resources.openRawResource(R.raw.data).bufferedReader().readLines().toList()
        val tmp = mutableListOf<DisplayCity>()
        val cities = mutableListOf<City>()

        for(i in lines.indices){
            if(i%3==2){
                tmp.add(DisplayCity((i/3)+1,lines[i], true))
                cities.add(City((i/3)+1, lines[i-2].toDouble(), lines[i-1].toDouble()))
            }
        }
        val adapter = CityAdapter(tmp)
        recycler.adapter= adapter

        addBtn = binding.runAlgorithm
        toggle = binding.matrixToggler

        addBtn.setOnClickListener {

            for(i in adapter.getSelectedCities()){
                cities.removeAll { it.index == i.index }
            }

            val path = if(toggle.isChecked) "android/time_preloaded.dat" else "android/distance_preloaded.dat"
            var matrix = if(toggle.isChecked) resources.openRawResource(R.raw.distance_preloaded).bufferedReader().readLines().toList() else resources.openRawResource(R.raw.time_preloaded).bufferedReader().readLines().toList()

            /*var filteredMatrix = mutableListOf<String>()
            for(i in 0 until cities.size){
                cities[i].index = i+1
                filteredMatrix.add(matrix[i])
            }*/

            val eilTsp = TSP(path, 1000000, cities, matrix)
            //val eilTsp = TSP("src/main/resources/bays29.tsp", 10000)
            val ga = GA(200, 0.8, 0.15)
            val bestPath: Tour = ga.execute(eilTsp)

            val route = mutableListOf<GeoPoint>()
            for(i in bestPath.path){
                route.add(GeoPoint(i.x, i.y))
            }

            val line = Polyline(map)
            line.setPoints(route)
            line.color = 0xFF0000FF.toInt()
            line.width = 5.0f
            map.overlays.add(line)
            map.invalidate()
        }


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
