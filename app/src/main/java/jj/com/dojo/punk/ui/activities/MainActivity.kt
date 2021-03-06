package jj.com.dojo.punk.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jj.com.dojo.punk.R
import jj.com.dojo.punk.domain.entities.Beer
import jj.com.dojo.punk.ui.adapters.PunkAdapter
import jj.com.dojo.punk.ui.utils.Data
import jj.com.dojo.punk.ui.utils.Status
import jj.com.dojo.punk.ui.viewmodels.PunkViewModel
import com.airbnb.lottie.LottieAnimationView
import org.koin.androidx.viewmodel.ext.android.viewModel

const val MINIMUM_LOADING_TIME = 1000L

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<PunkViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerAnimationLoader: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_beer_list)
        beerAnimationLoader = findViewById(R.id.loading_beer)

        viewModel.mainStateList.observe(::getLifecycle, ::updateUI)
        viewModel.mainStateDetail.observe(::getLifecycle, ::updateDetailUI)

        callStartService()
    }

    private fun updateUI(beersData: Data<List<Beer>>) {
        when (beersData.responseType) {
            Status.ERROR -> {
                hideLoading()
                beersData.error?.message?.let { showMessage(it) }
                beersData.data?.let { setBeerList(it) }
            }
            Status.LOADING -> {
                showLoading()
            }
            Status.SUCCESSFUL -> {
                beersData.data?.let { setBeerList(it) }
                hideLoading()
            }
        }
    }

    private fun updateDetailUI(beersData: Data<List<Beer>>) {
        when (beersData.responseType) {
            Status.ERROR -> {
                hideLoading()
                beersData.error?.message?.let { showMessage(it) }
            }
            Status.LOADING -> {
                showLoading()
            }
            Status.SUCCESSFUL -> {
                startActivity(Intent(this, DetailActivity::class.java))
                hideLoading()
            }
        }
    }

    private fun callStartService() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.onStartHome(1, 80)
        }, MINIMUM_LOADING_TIME)
    }

    private fun showLoading() {
        beerAnimationLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        beerAnimationLoader.visibility = View.GONE
    }

    private fun setBeerList(beerList: List<Beer>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val punkAdapter = PunkAdapter(beerList)
        punkAdapter.setOnItemClickListener(itemClickListener())
        recyclerView.adapter = punkAdapter
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun callClickDetailsService(item: Beer) {
        viewModel.onClickToBeerDetails(item.id, this@MainActivity.applicationContext)
    }

    private fun itemClickListener() = object : PunkAdapter.OnItemClickListener {
        override fun onItemClick(item: Beer) {
            callClickDetailsService(item)
        }
    }
}