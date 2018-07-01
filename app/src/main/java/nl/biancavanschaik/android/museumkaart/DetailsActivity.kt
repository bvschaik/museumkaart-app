package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_details.address
import kotlinx.android.synthetic.main.activity_details.content_group
import kotlinx.android.synthetic.main.activity_details.description
import kotlinx.android.synthetic.main.activity_details.error_group
import kotlinx.android.synthetic.main.activity_details.error_text
import kotlinx.android.synthetic.main.activity_details.events_group
import kotlinx.android.synthetic.main.activity_details.events_list
import kotlinx.android.synthetic.main.activity_details.exhibitions_group
import kotlinx.android.synthetic.main.activity_details.exhibitions_list
import kotlinx.android.synthetic.main.activity_details.opening_hours
import kotlinx.android.synthetic.main.activity_details.photo
import kotlinx.android.synthetic.main.activity_details.prices
import kotlinx.android.synthetic.main.activity_details.progress
import kotlinx.android.synthetic.main.activity_details.promotions_group
import kotlinx.android.synthetic.main.activity_details.promotions_list
import kotlinx.android.synthetic.main.activity_details.website
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.loadLargeImage
import nl.biancavanschaik.android.museumkaart.util.setHtmlText
import nl.biancavanschaik.android.museumkaart.view.ExhibitionRecyclerViewAdapter
import nl.biancavanschaik.android.museumkaart.view.ListingRecyclerViewAdapter
import org.koin.android.architecture.ext.viewModel

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        initRecyclerView(exhibitions_list)
        initRecyclerView(events_list)
        initRecyclerView(promotions_list)

        viewModel.museumId.value = intent.getStringExtra(ARG_MUSEUM_ID)
        viewModel.museumDetails.observe(this, Observer { showData(it) })
        viewModel.selectedListingId.observe(this, Observer { it?.let { openListing(it) } })
    }

    private fun initRecyclerView(recyclerView: RecyclerView) = recyclerView.apply {
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    private fun showData(result: Resource<Museum>?) {
        when (result?.status) {
            Resource.Status.LOADING -> showLoading()
            Resource.Status.SUCCESS -> result.data?.let { showDetails(it) }
            Resource.Status.ERROR -> showError(result.message ?: "Unknown error")
        }
    }

    private fun showLoading() {
        progress.visibility = View.VISIBLE
        error_group.visibility = View.GONE
        content_group.visibility = View.GONE
        exhibitions_group.visibility = View.GONE
        promotions_group.visibility = View.GONE
        events_group.visibility = View.GONE
    }

    private fun showError(message: String) {
        error_text.text = message
        error_group.visibility = View.VISIBLE
        progress.visibility = View.GONE
        content_group.visibility = View.GONE
        exhibitions_group.visibility = View.GONE
        promotions_group.visibility = View.GONE
        events_group.visibility = View.GONE
    }

    private fun showDetails(museum: Museum) {
        content_group.visibility = View.VISIBLE
        error_group.visibility = View.GONE
        progress.visibility = View.GONE

        val details = museum.details
        title = details.displayName

        description.setHtmlText(museum.permanentExhibition?.description)
        address.setHtmlText(arrayOf(details.address, details.city, details.telephone).filterNotNull().joinToString(separator = "<br>"))
        prices.setHtmlText(details.admissionPrice)
        opening_hours.setHtmlText(details.openingHours)
        if (details.website != null) {
            website.visibility = View.VISIBLE
            website.setOnClickListener {
                openWebsite(details.website)
            }
        } else {
            website.visibility = View.GONE
        }
        details.imagePath?.let { photo.loadLargeImage(it) }

        showExhibitions(museum.exhibitions)
        showEvents(museum.events)
        showPromotions(museum.promotions)
    }

    private fun showExhibitions(exhibitions: List<Listing>) {
        exhibitions_list.adapter = ExhibitionRecyclerViewAdapter(exhibitions)
        exhibitions_group.visibility = if (exhibitions.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showEvents(events: List<Listing>) {
        events_list.adapter = ListingRecyclerViewAdapter(viewModel, events)
        events_group.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showPromotions(promotions: List<Listing>) {
        promotions_list.adapter = ListingRecyclerViewAdapter(viewModel, promotions)
        promotions_group.visibility = if (promotions.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openWebsite(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
        val fullUrl = if (url.contains("://")) url else "http://$url"
        customTabsIntent.launchUrl(this, Uri.parse(fullUrl))
    }

    private fun openListing(listingId: String) {
        startActivity(ListingActivity.createIntent(this, listingId))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ARG_MUSEUM_ID = "museum_id"
        fun createIntent(context: Context, museum: String) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(ARG_MUSEUM_ID, museum)
        }
    }
}
