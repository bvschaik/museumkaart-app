package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_details.content_group
import kotlinx.android.synthetic.main.activity_details.description
import kotlinx.android.synthetic.main.activity_details.error_group
import kotlinx.android.synthetic.main.activity_details.error_text
import kotlinx.android.synthetic.main.activity_details.progress
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

    private fun showDetails(details: MuseumDetails) {
        description.text = details.displayname

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

    companion object {
        private const val ARG_MUSEUM_ID = "museum_id"
        fun createIntent(context: Context, page: String) = Intent(context, DetailsActivity::class.java).apply {
            putExtra(ARG_MUSEUM_ID, page)
        }
    }
}
