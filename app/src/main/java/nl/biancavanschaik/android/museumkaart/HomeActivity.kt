package nl.biancavanschaik.android.museumkaart

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_home.navigation
import kotlinx.android.synthetic.main.activity_home.visited_museums_list
import kotlinx.android.synthetic.main.activity_home.wish_list_museums_list
import nl.biancavanschaik.android.museumkaart.view.VisitedMuseumRecyclerViewAdapter
import nl.biancavanschaik.android.museumkaart.view.WishListMuseumRecyclerViewAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initRecyclerView(visited_museums_list)
        initRecyclerView(wish_list_museums_list)

        navigation.setOnNavigationItemSelectedListener(::itemSelected)

        viewModel.visitedMuseums.observe(this, Observer { museums ->
            museums?.let {
                visited_museums_list.adapter = VisitedMuseumRecyclerViewAdapter(viewModel, it)
            }
        })
        viewModel.wishListMuseums.observe(this, Observer { museums ->
            museums?.let {
                wish_list_museums_list.adapter = WishListMuseumRecyclerViewAdapter(viewModel, it)
            }
        })
        viewModel.selectedMuseumId.observe(this, Observer { id ->
            id?.let { startActivity(DetailsActivity.createIntent(this, it)) }
        })
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))
    }

    private fun itemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> showTab(Tab.MAP)
            R.id.navigation_visited -> showTab(Tab.VISITED)
            R.id.navigation_wish_list -> showTab(Tab.WISH_LIST)
            else -> return false
        }
        return true
    }

    private fun showTab(tab: Tab) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) ?: return
        when (tab) {
            Tab.MAP -> {
                supportFragmentManager.beginTransaction().show(mapFragment).commit()
                visited_museums_list.isVisible = false
                wish_list_museums_list.isVisible = false
            }
            Tab.VISITED -> {
                supportFragmentManager.beginTransaction().hide(mapFragment).commit()
                visited_museums_list.isVisible = true
                wish_list_museums_list.isVisible = false
            }
            Tab.WISH_LIST -> {
                supportFragmentManager.beginTransaction().hide(mapFragment).commit()
                visited_museums_list.isVisible = false
                wish_list_museums_list.isVisible = true
            }
        }
    }

    private enum class Tab {
        MAP,
        VISITED,
        WISH_LIST
    }
}