package nl.biancavanschaik.android.museumkaart.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.list_item_exhibition.view.description
import kotlinx.android.synthetic.main.list_item_exhibition.view.period
import kotlinx.android.synthetic.main.list_item_exhibition.view.title
import nl.biancavanschaik.android.museumkaart.R
import nl.biancavanschaik.android.museumkaart.data.rest.model.Exhibition
import nl.biancavanschaik.android.museumkaart.util.setHtmlText

class ExhibitionRecyclerViewAdapter(private val exhibitions: List<Exhibition>) : RecyclerView.Adapter<ExhibitionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_exhibition, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exhibition = exhibitions[position]
        holder.view.title.text = exhibition.name
        if (exhibition.startdate != null && exhibition.enddate != null) {
            holder.view.period.text = exhibition.startdate + " t/m " + exhibition.enddate
            holder.view.period.visibility = View.VISIBLE
        } else {
            holder.view.period.visibility = View.GONE
        }
        holder.view.description.setHtmlText(exhibition.description)
    }

    override fun getItemCount(): Int {
        return exhibitions.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
