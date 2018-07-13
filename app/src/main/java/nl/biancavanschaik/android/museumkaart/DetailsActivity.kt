package nl.biancavanschaik.android.museumkaart

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
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
import kotlinx.android.synthetic.main.activity_details.visited
import kotlinx.android.synthetic.main.activity_details.website
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.util.IsoDate
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.loadLargeImage
import nl.biancavanschaik.android.museumkaart.util.setHtmlText
import nl.biancavanschaik.android.museumkaart.view.ListingRecyclerViewAdapter
import org.koin.android.architecture.ext.viewModel

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        title = intent.getStringExtra(ARG_MUSEUM_NAME)

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
        progress.isVisible = true
        error_group.isVisible = false
        content_group.isVisible = false
        exhibitions_group.isVisible = false
        promotions_group.isVisible = false
        events_group.isVisible = false
    }

    private fun showError(message: String) {
        error_text.text = message
        error_group.isVisible = true
        progress.isVisible = false
        content_group.isVisible = false
        exhibitions_group.isVisible = false
        promotions_group.isVisible = false
        events_group.isVisible = false
    }

    private fun showDetails(museum: Museum) {
        content_group.isVisible = true
        error_group.isVisible = false
        progress.isVisible = false

        val details = museum.details
        title = details.name
        invalidateOptionsMenu()

        visited.text = details.visitedOn?.let { getString(R.string.details_visited_on, it.toHumanString()) }
        description.setHtmlText(museum.permanentExhibition?.description)
        address.setHtmlText(arrayOf(details.address, details.city, details.telephone).filterNotNull().joinToString(separator = "<br>"))
        prices.setHtmlText(details.admissionPrice)
        opening_hours.setHtmlText(details.openingHours)
        if (details.website != null) {
            website.isVisible = true
            website.setOnClickListener {
                openWebsite(details.website)
            }
        } else {
            website.isVisible = false
        }
        details.imagePath?.let { photo.loadLargeImage(it) }

        showExhibitions(museum.exhibitions)
        showEvents(museum.events)
        showPromotions(museum.promotions)
    }

    private fun showExhibitions(exhibitions: List<Listing>) {
        exhibitions_list.adapter = ListingRecyclerViewAdapter(viewModel, exhibitions)
        exhibitions_group.isVisible = exhibitions.isNotEmpty()
    }

    private fun showEvents(events: List<Listing>) {
        events_list.adapter = ListingRecyclerViewAdapter(viewModel, events)
        events_group.isVisible = events.isNotEmpty()
    }

    private fun showPromotions(promotions: List<Listing>) {
        promotions_list.adapter = ListingRecyclerViewAdapter(viewModel, promotions)
        promotions_group.isVisible = promotions.isNotEmpty()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_toolbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val details = viewModel.museumDetails.value?.data?.details
        menu.findItem(R.id.menu_add_to_wish_list)?.isVisible = details?.wishList == false
        menu.findItem(R.id.menu_remove_from_wish_list)?.isVisible = details?.wishList == true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_add_to_wish_list -> viewModel.setWishList(true)
            R.id.menu_remove_from_wish_list -> viewModel.setWishList(false)
            R.id.menu_mark_visited -> showMarkVisitedDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showMarkVisitedDialog() {
        val dateVisited = viewModel.museumDetails.value?.data?.details?.visitedOn ?: IsoDate.today()
        showDatePicker(dateVisited, { viewModel.setVisitedOn(it) }, { viewModel.setVisitedOn(null) })
    }

    private fun showDatePicker(initialDate: IsoDate, onSet: (IsoDate) -> Unit, onClear: () -> Unit) {
        DatePickerDialog(this, { _, year, month0, day ->
            onSet(IsoDate(year, month0 + 1, day))
        }, initialDate.year, initialDate.month - 1, initialDate.day)
                .apply { setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_clear)) { _, _ -> onClear() } }
                .show()
    }

    companion object {
        private const val ARG_MUSEUM_ID = "museum_id"
        private const val ARG_MUSEUM_NAME = "museum_name"
        fun createIntent(context: Context, museumId: String, museumName: String) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(ARG_MUSEUM_ID, museumId)
            putExtra(ARG_MUSEUM_NAME, museumName)
        }
    }
}
