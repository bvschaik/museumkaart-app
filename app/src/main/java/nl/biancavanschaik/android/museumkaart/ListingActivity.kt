package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_listing.description
import kotlinx.android.synthetic.main.activity_listing.error_group
import kotlinx.android.synthetic.main.activity_listing.listing_title
import kotlinx.android.synthetic.main.activity_listing.opening_hours
import kotlinx.android.synthetic.main.activity_listing.opening_hours_title
import kotlinx.android.synthetic.main.activity_listing.period
import kotlinx.android.synthetic.main.activity_listing.period_title
import kotlinx.android.synthetic.main.activity_listing.photo
import kotlinx.android.synthetic.main.activity_listing.prices
import kotlinx.android.synthetic.main.activity_listing.prices_title
import kotlinx.android.synthetic.main.activity_listing.progress
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.util.loadLargeImage
import nl.biancavanschaik.android.museumkaart.util.setHtmlText
import org.koin.android.viewmodel.ext.android.viewModel

class ListingActivity : AppCompatActivity() {

    private val viewModel by viewModel<ListingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        error_group.isVisible = false
        progress.isVisible = true
        setContentVisible(false)

        viewModel.listingId.value = intent.getStringExtra(ARG_LISTING_ID)
        viewModel.listing.observe(this, Observer { showListing(it) })
    }

    private fun setContentVisible(visible: Boolean) {
        listing_title.isVisible = visible
        photo.isVisible = visible
        description.isVisible = visible
        period_title.isVisible = visible
        period.isVisible = visible
        opening_hours_title.isVisible = visible
        opening_hours.isVisible = visible
        prices_title.isVisible = visible
        prices.isVisible = visible
    }

    private fun showListing(listing: Listing?) {
        if (listing == null) return finish()

        error_group.isVisible = false
        progress.isVisible = false
        setContentVisible(true)

        title = when (listing.type) {
            Listing.Type.EVENT -> getString(R.string.listing_title_event)
            Listing.Type.EXHIBITION -> getString(R.string.listing_title_exhibition)
            Listing.Type.PROMOTION -> getString(R.string.listing_title_promotion)
            Listing.Type.PERMANENT -> getString(R.string.listing_title_permanent)
        }

        listing_title.text = listing.name
        listing.imagePath?.let { photo.loadLargeImage(listing.imagePath) }
        description.setHtmlText(listing.description)

        val hasPeriod = listing.startDate != null && listing.endDate != null
        if (hasPeriod) {
            period.text = getString(R.string.period_description,
                    listing.startDate!!.toHumanString(), listing.endDate!!.toHumanString())
        }
        period_title.isVisible = hasPeriod
        period.isVisible = hasPeriod

        opening_hours.setHtmlText(listing.openingHours)
        opening_hours.isVisible = listing.openingHours != null
        opening_hours_title.isVisible = listing.openingHours != null

        prices.setHtmlText(listing.admissionPrice)
        prices.isVisible = listing.admissionPrice != null
        prices_title.isVisible = listing.admissionPrice != null
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
        private const val ARG_LISTING_ID = "listing_id"
        fun createIntent(context: Context, listing: String) = Intent(context, ListingActivity::class.java).apply {
            putExtra(ARG_LISTING_ID, listing)
        }
    }
}
