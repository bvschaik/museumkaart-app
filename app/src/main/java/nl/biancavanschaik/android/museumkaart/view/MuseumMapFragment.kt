package nl.biancavanschaik.android.museumkaart.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.fragment_museum_map.my_location_fab
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import nl.biancavanschaik.android.museumkaart.HomeViewModel
import nl.biancavanschaik.android.museumkaart.R
import nl.biancavanschaik.android.museumkaart.data.CameraPreferences
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary
import nl.biancavanschaik.android.museumkaart.map.MuseumItem
import nl.biancavanschaik.android.museumkaart.util.getBitmapFromVectorDrawable
import nl.biancavanschaik.android.museumkaart.util.hasPermission
import nl.biancavanschaik.android.museumkaart.util.requestPermission
import org.koin.android.architecture.ext.sharedViewModel

class MuseumMapFragment: Fragment() {
    private val viewModel by sharedViewModel<HomeViewModel>()

    private var map: GoogleMap? = null
    private val museumItems = mutableMapOf<String, MuseumItem>()
    private var myLocationButton: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_museum_map, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.goole_map_fragment) as SupportMapFragment
        myLocationButton = mapFragment.view!!.findViewById("2".toInt())
        mapFragment.getMapAsync(::onMapReady)
    }

    override fun onStop() {
        map?.cameraPosition?.let { CameraPreferences(requireContext()).save(it) }
        super.onStop()
    }

    private fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        // Disable the navigate and open in google maps functions
        googleMap.uiSettings.isMapToolbarEnabled = false

        setupNavigatingToMyPosition(googleMap)

        val clusterManager = setupClusterManager(googleMap)
        viewModel.allMuseums.observe(this, Observer {
            it?.data?.let { showMuseums(it, clusterManager) }
        })

        setupCameraPosition(googleMap)

        googleMap.setOnCameraMoveStartedListener { reason ->
            if (reason != GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                my_location_fab.setImageDrawable(requireContext().getDrawable(R.drawable.ic_my_location_inactive))
            }
        }
    }

    private fun setupCameraPosition(googleMap: GoogleMap) {
        val camera = CameraPreferences(requireContext()).load()
        if (camera == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(52.0, 5.0)))
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(6.0f))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera))
        }
    }

    private fun setupNavigatingToMyPosition(googleMap: GoogleMap) {
        my_location_fab.visibility = View.VISIBLE
        if (requireContext().hasPermission(ACCESS_FINE_LOCATION)) {
            enableNavigatingToPosition(googleMap)
        } else {
            my_location_fab.setOnClickListener {
                requireActivity().requestPermission(ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNavigatingToPosition(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        myLocationButton?.visibility = View.GONE
        my_location_fab.setOnClickListener {
            navigateToMyPosition()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_LOCATION
                && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map?.let {
                enableNavigatingToPosition(it)
                launch(UI) {
                    delay(100)
                    navigateToMyPosition()
                }
            }
        }
    }

    private fun navigateToMyPosition() {
        my_location_fab.setImageDrawable(requireContext().getDrawable(R.drawable.ic_my_location_active))
        myLocationButton?.callOnClick()
    }

    private fun setupClusterManager(googleMap: GoogleMap): ClusterManager<MuseumItem> {
        val clusterManager = ClusterManager<MuseumItem>(requireContext(), googleMap)
        clusterManager.renderer = object : DefaultClusterRenderer<MuseumItem>(requireContext(), googleMap, clusterManager) {
            override fun onBeforeClusterItemRendered(item: MuseumItem, markerOptions: MarkerOptions) {
                markerOptions.icon(item.icon)
            }
        }
        clusterManager.setOnClusterItemInfoWindowClickListener {
            viewModel.selectedMuseumId.value = it.id
        }
        googleMap.setOnInfoWindowClickListener(clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        return clusterManager
    }

    private fun showMuseums(museums: List<MuseumSummary>, clusterManager: ClusterManager<MuseumItem>) {
        Log.d("MAP", "Updating with ${museums.size} museums")
        val wishListIcon = BitmapDescriptorFactory.fromBitmap(requireContext().getBitmapFromVectorDrawable(R.drawable.ic_marker_purple))
        val unvisitedIcon = BitmapDescriptorFactory.fromBitmap(requireContext().getBitmapFromVectorDrawable(R.drawable.ic_marker_red))
        val visitedIcon = BitmapDescriptorFactory.fromBitmap(requireContext().getBitmapFromVectorDrawable(R.drawable.ic_marker_green))

        // remove deleted museums
        val toRemove = museumItems - museums.map { it.id }
        toRemove.forEach { museumItems.remove(it.key); clusterManager.removeItem(it.value) }

        museums.forEach {museum ->
            if (museum.lat != null && museum.lon != null) {
                val position = LatLng(museum.lat, museum.lon)
                val visitedText = if (museum.visitedOn != null) "${museum.city} (${museum.visitedOn.toHumanString()})" else museum.city
                val icon = when {
                    museum.visitedOn != null -> visitedIcon
                    museum.wishList -> wishListIcon
                    else -> unvisitedIcon
                }
                clusterManager.addItem(MuseumItem(museum.id, position, museum.name, visitedText, icon))
            }
        }
    }
}

private const val MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1