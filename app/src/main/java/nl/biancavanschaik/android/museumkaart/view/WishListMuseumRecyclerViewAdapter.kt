package nl.biancavanschaik.android.museumkaart.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_listing.view.title
import kotlinx.android.synthetic.main.list_item_museum.view.description
import nl.biancavanschaik.android.museumkaart.HomeViewModel
import nl.biancavanschaik.android.museumkaart.R
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary

class WishListMuseumRecyclerViewAdapter(
        private val viewModel: HomeViewModel,
        private val museums: List<MuseumSummary>
) : RecyclerView.Adapter<MuseumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuseumViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_museum, parent, false)
        return MuseumViewHolder(view)
    }

    override fun onBindViewHolder(holder: MuseumViewHolder, position: Int) {
        val museum = museums[position]
        holder.view.title.text = museum.name
        holder.view.description.text = museum.city
        holder.view.setOnClickListener { viewModel.selectedMuseumId.value = museum.id }
    }

    override fun getItemCount(): Int {
        return museums.size
    }
}
