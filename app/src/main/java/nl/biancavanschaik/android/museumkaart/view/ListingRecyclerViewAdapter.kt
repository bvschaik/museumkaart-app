package nl.biancavanschaik.android.museumkaart.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_listing.view.period
import kotlinx.android.synthetic.main.list_item_listing.view.photo
import kotlinx.android.synthetic.main.list_item_listing.view.title
import nl.biancavanschaik.android.museumkaart.DetailsViewModel
import nl.biancavanschaik.android.museumkaart.R
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.util.loadSmallImage

class ListingRecyclerViewAdapter(
        private val viewModel: DetailsViewModel,
        private val listings: List<Listing>
) : RecyclerView.Adapter<ListingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[position]
        holder.view.title.text = listing.name
        if (listing.startDate != null && listing.endDate != null) {
            holder.view.period.text = holder.view.context.getString(
                    R.string.period_description,
                    listing.startDate.toHumanString(),
                    listing.endDate.toHumanString()
            )
            holder.view.period.visibility = View.VISIBLE
        } else {
            holder.view.period.visibility = View.GONE
        }
        if (listing.imagePath != null) {
            holder.view.photo.loadSmallImage(listing.imagePath)
        } else {
            holder.view.photo.setImageResource(0)
        }
        holder.view.setOnClickListener { viewModel.selectedListingId.value = listing.id }
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
