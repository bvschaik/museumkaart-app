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
import android.view.View
import kotlinx.android.synthetic.main.activity_details.address
import kotlinx.android.synthetic.main.activity_details.content_group
import kotlinx.android.synthetic.main.activity_details.description
import kotlinx.android.synthetic.main.activity_details.error_group
import kotlinx.android.synthetic.main.activity_details.error_text
import kotlinx.android.synthetic.main.activity_details.exhibitions_group
import kotlinx.android.synthetic.main.activity_details.exhibitions_list
import kotlinx.android.synthetic.main.activity_details.opening_hours
import kotlinx.android.synthetic.main.activity_details.photo
import kotlinx.android.synthetic.main.activity_details.prices
import kotlinx.android.synthetic.main.activity_details.progress
import kotlinx.android.synthetic.main.activity_details.website
import nl.biancavanschaik.android.museumkaart.data.rest.model.Exhibition
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.loadLargeImage
import nl.biancavanschaik.android.museumkaart.util.setHtmlText
import nl.biancavanschaik.android.museumkaart.view.ExhibitionRecyclerViewAdapter
import org.koin.android.architecture.ext.viewModel

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        exhibitions_list.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.museumId.value = intent.getStringExtra(ARG_MUSEUM_ID)
        viewModel.museumDetails.observe(this, Observer { showData(it) })
    }

    private fun showData(result: Resource<MuseumDetails>?) {
        when (result?.status) {
            Resource.Status.LOADING -> showLoading()
            Resource.Status.SUCCESS -> result.data?.let { showDetails(it) }
            Resource.Status.ERROR -> showError(result.message ?: "Unknown error")
        }
    }

    private fun showLoading() {
        progress.visibility = View.VISIBLE
        content_group.visibility = View.GONE
        error_group.visibility = View.GONE
    }

    private fun showDetails(museum: MuseumDetails) {
        content_group.visibility = View.VISIBLE
        error_group.visibility = View.GONE
        progress.visibility = View.GONE

        title = museum.displayname

        description.setHtmlText(museum.listings.permanent.firstOrNull()?.description)
        address.setHtmlText(arrayOf(museum.streetandnumber, museum.city, museum.telephone).filterNotNull().joinToString(separator = "<br>"))
        prices.setHtmlText(museum.admissionprice)
        opening_hours.setHtmlText(museum.openinghours)
        if (museum.website != null) {
            website.visibility = View.VISIBLE
            website.setOnClickListener {
                openWebsite(museum.website)
            }
        } else {
            website.visibility = View.GONE
        }
        museum.path?.let { photo.loadLargeImage(it) }

        showExhibitions(museum.listings.exhibition)
    }

    private fun showExhibitions(exhibitions: List<Exhibition>) {
        exhibitions_list.adapter = ExhibitionRecyclerViewAdapter(exhibitions)
        exhibitions_group.visibility = if (exhibitions.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openWebsite(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
        val fullUrl = if (url.contains("://")) url else "http://$url"
        customTabsIntent.launchUrl(this, Uri.parse(fullUrl))
    }

    private fun showError(message: String) {
        error_text.text = message
        error_group.visibility = View.VISIBLE
        content_group.visibility = View.GONE
        progress.visibility = View.GONE
    }

    companion object {
        private const val ARG_MUSEUM_ID = "museum_id"
        fun createIntent(context: Context, page: String) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(ARG_MUSEUM_ID, page)
        }
    }
}
