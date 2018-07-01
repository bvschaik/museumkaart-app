package nl.biancavanschaik.android.museumkaart.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_exhibition.view.description
import kotlinx.android.synthetic.main.list_item_exhibition.view.period
import kotlinx.android.synthetic.main.list_item_exhibition.view.photo
import kotlinx.android.synthetic.main.list_item_exhibition.view.title
import nl.biancavanschaik.android.museumkaart.R
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.util.loadSmallImage
import nl.biancavanschaik.android.museumkaart.util.setHtmlText

class ExhibitionRecyclerViewAdapter(private val exhibitions: List<Listing>) : RecyclerView.Adapter<ExhibitionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_exhibition, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exhibition = exhibitions[position]
        holder.view.title.text = exhibition.name
        if (exhibition.startDate != null && exhibition.endDate != null) {
            holder.view.period.text = holder.view.context.getString(
                    R.string.period_description,
                    exhibition.startDate.toHumanString(),
                    exhibition.endDate.toHumanString()
            )
            holder.view.period.visibility = View.VISIBLE
        } else {
            holder.view.period.visibility = View.GONE
        }
        holder.view.description.setHtmlText(exhibition.description)
        if (exhibition.imagePath != null) {
            holder.view.photo.loadSmallImage(exhibition.imagePath)
        } else {
            holder.view.photo.setImageResource(0)
        }
    }

    override fun getItemCount(): Int {
        return exhibitions.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
