package nl.biancavanschaik.android.museumkaart.map

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MuseumItem(
        val id: String,
        private val position: LatLng,
        private val title: String,
        private val snippet: String,
        val icon: BitmapDescriptor) : ClusterItem {

    override fun getPosition() = position

    override fun getTitle() = title

    override fun getSnippet() = snippet
}