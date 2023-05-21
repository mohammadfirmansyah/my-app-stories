package com.dicoding.myappstories.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.FragmentMapsPageBinding
import com.dicoding.myappstories.ui.viewmodel.MapsPageViewModel
import com.dicoding.myappstories.utilities.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@ExperimentalPagingApi
@AndroidEntryPoint
class MapsPageFragment : Fragment() {

    private var _binding: FragmentMapsPageBinding? = null
    private val binding get() = _binding!!
    private val mapsPageViewModel: MapsPageViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var token: String
    private lateinit var pref: SessionManager
    private lateinit var mMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map))

        getDeviceStoryLocation()
        initMarkStoryLocation()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        pref = SessionManager(requireContext())
        token = pref.fetchAuthToken().toString()

        initAction()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }


    private fun initAction() {
        binding.apply {
            btnAccount.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_mapsFragment_to_profileFragment)
            )
        }
    }

    private fun getDeviceStoryLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.activate_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initMarkStoryLocation() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                mapsPageViewModel.getStoriesLocation(token).collect { result ->
                    result.onSuccess { response ->
                        response.listStory.forEach { story ->

                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)
                                val color =
                                    ContextCompat.getColor(requireContext(), R.color.light_orange)
                                val hsv = FloatArray(3)
                                Color.colorToHSV(color, hsv)
                                val blueSkyMarker = hsv[0]

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet("Lat: ${story.lat}, Lon: ${story.lon}")
                                        .icon(BitmapDescriptorFactory.defaultMarker(blueSkyMarker))
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getDeviceStoryLocation()
            }
        }
}