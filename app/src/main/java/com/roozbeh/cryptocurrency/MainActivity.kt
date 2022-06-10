package com.roozbeh.cryptocurrency

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.roozbeh.cryptocurrency.adapter.CryptoAdapter
import com.roozbeh.cryptocurrency.adapter.GalleryAdapterShimmer
import com.roozbeh.cryptocurrency.databinding.ActivityMainBinding
import com.simform.refresh.SSPullToRefreshLayout
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.constant.Currency
import drewcarlson.coingecko.models.coins.CoinMarkets
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var coinGecko: CoinGeckoClient? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        intent = Intent(this, DetailsActivity::class.java)
        initRecycler()
        binding.shimmer.startShimmer()
        coinGecko = CoinGeckoClient.create()

        lifecycleScope.launch {
            callGetApi()
        }


        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                lifecycleScope.launch {
                    callGetApi()
                }
            }
        })




    }

    private fun initRecycler() {
        binding.recyclerCrypto.layoutManager = LinearLayoutManager(this)
        binding.recyShimmer.layoutManager = LinearLayoutManager(this)
        val adapterShimmer = GalleryAdapterShimmer(this)
        binding.recyShimmer.adapter = adapterShimmer
        binding.shimmer.startShimmer()
    }

    private suspend fun callGetApi() {
        if (Utils.checkForInternet(applicationContext)) {
            try {

                coinGecko!!.ping().geckoSays
                var coinMarketsList = coinGecko!!.getCoinMarkets(Currency.USD)
                var coinmarkets = ArrayList<CoinMarkets>(coinMarketsList.markets)

                stopShimmer()

                val adapter = CryptoAdapter(coinmarkets, applicationContext,
                    object : CryptoAdapter.OnItemClickListener {
                        override fun onItemClick(
                            id: String,
                            marketCap: String,
                            currentPrice: String,
                            priceChangePercentage24h: Double
                        ) {
                            intent.putExtra("id", id)
                            intent.putExtra("marketCap", marketCap)
                            intent.putExtra("currentPrice", currentPrice)
                            intent.putExtra("priceChangePercentage24h", priceChangePercentage24h)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out_new)
                        }

                    })
                binding.recyclerCrypto.adapter = adapter
            } catch (e: Exception) {
                stopShimmer()
                e.printStackTrace()
            }
        } else {

            stopShimmer()
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.internetNotAvailable),
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    private fun stopShimmer() {


        binding.shimmer.stopShimmer()
        binding.shimmer.visibility = View.GONE
        binding.ssPullRefresh.visibility = View.VISIBLE
        binding.ssPullRefresh.setRefreshing(false)
    }


}