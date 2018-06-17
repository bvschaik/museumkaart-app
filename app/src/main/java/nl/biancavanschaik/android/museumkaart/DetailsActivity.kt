package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_details.address
import kotlinx.android.synthetic.main.activity_details.content_group
import kotlinx.android.synthetic.main.activity_details.description
import kotlinx.android.synthetic.main.activity_details.error_group
import kotlinx.android.synthetic.main.activity_details.error_text
import kotlinx.android.synthetic.main.activity_details.opening_hours
import kotlinx.android.synthetic.main.activity_details.photo
import kotlinx.android.synthetic.main.activity_details.prices
import kotlinx.android.synthetic.main.activity_details.progress
import kotlinx.android.synthetic.main.activity_details.website
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.Resource
import org.koin.android.architecture.ext.viewModel

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

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
        title = museum.displayname

        description.setOptionalText(museum.listings.permanent.firstOrNull()?.description)
        address.setOptionalText(arrayOf(museum.streetandnumber, museum.city, museum.telephone).filterNotNull().joinToString(separator = "<br>"))
        prices.setOptionalText(museum.admissionprice)
        opening_hours.setOptionalText(museum.openinghours)
        if (museum.website != null) {
            website.visibility = View.VISIBLE
            website.setOnClickListener {
                //openWebsite(museum.website)
            }
        } else {
            website.visibility = View.GONE
        }
        museum.path?.let { photo.loadImage(it) }

        content_group.visibility = View.VISIBLE
        error_group.visibility = View.GONE
        progress.visibility = View.GONE
    }

    private fun showError(message: String) {
        error_text.text = message
        error_group.visibility = View.VISIBLE
        content_group.visibility = View.GONE
        progress.visibility = View.GONE
    }

    private fun TextView.setOptionalText(text: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.text = Html.fromHtml(text ?: "?", Html.FROM_HTML_MODE_COMPACT)
        } else {
            this.text = Html.fromHtml(text ?: "?")
        }
    }

    private fun ImageView.loadImage(imagePath: String) {
        val fullUrl = Uri.parse("https://cdn.museum.nl/cards/700x394").buildUpon().appendPath(imagePath)
        //Picasso.with(this.context).load(fullUrl.build()).into(photo)
    }

    companion object {
        private const val ARG_MUSEUM_ID = "museum_id"
        fun createIntent(context: Context, page: String) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(ARG_MUSEUM_ID, page)
        }
    }
}
