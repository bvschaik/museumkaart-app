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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.activity_home.navigation
import kotlinx.android.synthetic.main.activity_home.visited_museums_list
import kotlinx.android.synthetic.main.activity_home.wish_list_museums_list
import nl.biancavanschaik.android.museumkaart.data.CameraPreferences
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary
import nl.biancavanschaik.android.museumkaart.map.MuseumItem
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
    private val museumItems = mutableMapOf<String, MuseumItem>()

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
        val clusterManager = setupClusterManager(googleMap)

        viewModel.allMuseums.observe(this, Observer {
            it?.data?.let { showMuseums(it, clusterManager) }
        })
        val camera = CameraPreferences(this).load()
        if (camera == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(52.0, 5.0)))
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(6.0f))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera))
        }
    }

    private fun setupClusterManager(googleMap: GoogleMap): ClusterManager<MuseumItem> {
        val clusterManager = ClusterManager<MuseumItem>(this, googleMap)
        clusterManager.renderer = object : DefaultClusterRenderer<MuseumItem>(this, googleMap, clusterManager) {
            override fun onBeforeClusterItemRendered(item: MuseumItem, markerOptions: MarkerOptions) {
                markerOptions.icon(item.icon)
            }
        }
        clusterManager.setOnClusterItemInfoWindowClickListener {
            openDetails(it.id)
        }
        googleMap.setOnInfoWindowClickListener(clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        return clusterManager
    }

    private fun showMuseums(museums: List<MuseumSummary>, clusterManager: ClusterManager<MuseumItem>) {
        Log.d("MAP", "Updating with ${museums.size} museums")
        val wishListIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_purple))
        val unvisitedIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_red))
        val visitedIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_marker_green))

        // remove deleted museums
        val toRemove = museumItems - museums.map { it.id }
        toRemove.forEach { key, item -> museumItems.remove(key); clusterManager.removeItem(item) }

        museums.forEach {
            if (it.lat != null && it.lon != null) {
                val position = LatLng(it.lat, it.lon)
                val visitedText = if (it.visitedOn != null) "${it.city} (${it.visitedOn.toHumanString()})" else it.city
                val icon = when {
                    it.visitedOn != null -> visitedIcon
                    it.wishList -> wishListIcon
                    else -> unvisitedIcon
                }
                clusterManager.addItem(MuseumItem(it.id, position, it.name, visitedText, icon))
            }
        }
    }

    private fun openDetails(museumId: String) {
        startActivity(DetailsActivity.createIntent(this, museumId))
    }
}
