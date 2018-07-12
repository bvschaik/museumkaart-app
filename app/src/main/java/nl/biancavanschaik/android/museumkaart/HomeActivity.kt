package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_home.navigation
import kotlinx.android.synthetic.main.activity_home.visited_museums_list
import kotlinx.android.synthetic.main.activity_home.wish_list_museums_list
import nl.biancavanschaik.android.museumkaart.data.CameraPreferences
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary
import nl.biancavanschaik.android.museumkaart.util.getBitmapFromVectorDrawable
import nl.biancavanschaik.android.museumkaart.view.VisitedMuseumRecyclerViewAdapter
import nl.biancavanschaik.android.museumkaart.view.WishListMuseumRecyclerViewAdapter
import org.koin.android.architecture.ext.viewModel

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    enum class Tab {
        MAP,
        VISITED,
        WISH_LIST
    }

    private val viewModel by viewModel<HomeViewModel>()

    private var map: GoogleMap? = null
    private val mapMarkers = mutableMapOf<String, Marker>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> showTab(Tab.MAP)
            R.id.navigation_visited -> showTab(Tab.VISITED)
            R.id.navigation_wish_list -> showTab(Tab.WISH_LIST)
            else -> return@OnNavigationItemSelectedListener false
        }
        true
    }

    private fun showTab(tab: Tab) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment)
        when (tab) {
            Tab.MAP -> {
                supportFragmentManager.beginTransaction().show(mapFragment).commit()
                visited_museums_list.isVisible = false
                wish_list_museums_list.isVisible = false
            }
            Tab.VISITED -> {
                supportFragmentManager.beginTransaction().hide(mapFragment).commit()
                visited_museums_list.isVisible = true
                wish_list_museums_list.isVisible = false
            }
            Tab.WISH_LIST -> {
                supportFragmentManager.beginTransaction().hide(mapFragment).commit()
                visited_museums_list.isVisible = false
                wish_list_museums_list.isVisible = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.visitedMuseums.observe(this, Observer { it?.let {
            visited_museums_list.adapter = VisitedMuseumRecyclerViewAdapter(viewModel, it)
        }})
        viewModel.wishListMuseums.observe(this, Observer { it?.let {
            wish_list_museums_list.adapter = WishListMuseumRecyclerViewAdapter(viewModel, it)
        }})
    }

    override fun onStop() {
        map?.cameraPosition?.let { CameraPreferences(this).save(it) }
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        googleMap.setOnInfoWindowClickListener { openDetails(it.tag as String) }

        viewModel.allMuseums.observe(this, Observer {
            it?.data?.let { showMuseums(it, googleMap) }
        })
        val camera = CameraPreferences(this).load()
        if (camera == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(52.0, 5.0)))
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(6.0f))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera))
        }
    }

    private fun showMuseums(museums: List<MuseumSummary>, googleMap: GoogleMap) {
        Log.d("MAP", "Updating with ${museums.size} museums")
        val wishListIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_purple))
        val unvisitedIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_red))
        val visitedIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_green))

        // remove deleted museums
        val toRemove = mapMarkers - museums.map { it.id }
        toRemove.forEach { key, marker -> mapMarkers.remove(key); marker.remove() }

        museums.forEach {
            if (it.lat != null && it.lon != null) {
                val position = LatLng(it.lat, it.lon)
                val visitedText = if (it.visitedOn != null) "${it.city} (${it.visitedOn.toHumanString()})" else it.city
                val icon = when {
                    it.visitedOn != null -> visitedIcon
                    it.wishList -> wishListIcon
                    else -> unvisitedIcon
                }
                val existingMarker = mapMarkers[it.id]
                if (existingMarker == null) {
                    mapMarkers[it.id] = googleMap.addMarker(MarkerOptions()
                            .position(position)
                            .title(it.name)
                            .snippet(visitedText)
                            .icon(icon)
                            .anchor(0.5f, 0.5f)
                    ).apply { tag = it.id }
                } else {
                    existingMarker.position = position
                    existingMarker.title = it.name
                    existingMarker.snippet = visitedText
                    existingMarker.setIcon(icon)
                }
            }
        }
    }

    private fun openDetails(museumId: String) {
        startActivity(DetailsActivity.createIntent(this, museumId))
    }
}
