package com.example.pametnipaketnik.map

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pametnipaketnik.API.Map.MapInterface
import com.example.pametnipaketnik.R
import com.example.pametnipaketnik.databinding.FragmentMapPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
