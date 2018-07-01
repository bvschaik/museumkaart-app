package nl.biancavanschaik.android.museumkaart

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)
    }

    companion object {
        private const val ARG_LISTING_ID = "listing_id"
        fun createIntent(context: Context, listing: String) = Intent(context, ListingActivity::class.java).apply {
            putExtra(ARG_LISTING_ID, listing)
        }
    }
}
