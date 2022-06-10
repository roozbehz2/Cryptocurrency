package com.roozbeh.cryptocurrency

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.roozbeh.cryptocurrency.Utils.checkForInternet
import com.roozbeh.cryptocurrency.databinding.ActivityDetailsBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.constant.Currency
import drewcarlson.coingecko.models.coins.CoinMarkets
import drewcarlson.coingecko.models.coins.MarketChart
import kotlinx.coroutines.launch
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class DetailsActivity : AppCompatActivity() {

    private var coinGecko: CoinGeckoClient? = null
    private lateinit var binding: ActivityDetailsBinding
    private var id: String = ""
    var data1Day: MarketChart? = null
    var data2Day: MarketChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_details)


        binding =
            DataBindingUtil.setContentView<ActivityDetailsBinding>(this, R.layout.activity_details)
        try {

            val bundle: Bundle = intent.extras!!
            id = bundle.getString("id")!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        coinGecko = CoinGeckoClient.create()

        lifecycleScope.launch {
            callGetApi()
        }

        binding.time.selectButton(binding.btn1.id)
        binding.time.setOnSelectListener { button: ThemedButton ->
            // handle selected button
            if (checkForInternet(applicationContext)) {
                try {

                    lifecycleScope.launch {
                        if (button.text == "1D") {

                            if (data1Day == null) {

                                data1Day = coinGecko!!.getCoinMarketChartRangeById(
                                    id,
                                    Currency.USD,
                                    (Utils.getDaysAgo(2) / 1000L).toString(),
                                    (System.currentTimeMillis() / 1000L).toString()
                                )
                            }
                            chart(
                                getData(
                                    data1Day!!
                                )
                            )


                        } else if (button.text == "2D") {

                            if (data2Day == null) {
                                data2Day = coinGecko!!.getCoinMarketChartRangeById(
                                    id,
                                    Currency.USD,
                                    (Utils.getDaysAgo(1) / 1000L).toString(),
                                    (System.currentTimeMillis() / 1000L).toString()
                                )
                            }
                            chart(
                                getData(
                                    data2Day!!
                                )
                            )


                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }else{

                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.internetNotAvailable),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private suspend fun callGetApi() {

        if (checkForInternet(applicationContext)) {
            try {

                coinGecko!!.ping().geckoSays

                val coinMarketsList = coinGecko!!.getCoinById(id, marketData = true)
                binding.txtNameCoinDetails.text = coinMarketsList.name


                Glide
                    .with(applicationContext)
                    .load(coinMarketsList.image.large)
                    .placeholder(R.drawable.ic__placeholder)
                    .into(binding.imgCoinDetails)

                binding.txtPriceCoinDetail.text =
                    "$" + coinMarketsList.marketData!!.currentPrice!!["usd"]

                if (coinMarketsList.marketData!!.athChangePercentage!!["usd"]!! > 0) {
                    binding.txtPercentageDetail.setTextColor(applicationContext.getColor(R.color.teal_200))
                } else if (coinMarketsList.marketData!!.athChangePercentage!!["usd"]!! < 0) {
                    binding.txtPercentageDetail.setTextColor(applicationContext.getColor(R.color.red))
                } else {
                    binding.txtPercentageDetail.setTextColor(applicationContext.getColor(R.color.white))
                }
                binding.txtPercentageDetail.text =
                    coinMarketsList.marketData!!.priceChangePercentage24h.toString() + "%"

                binding.txtNameCoinDetail.text = coinMarketsList.symbol + " | " + Currency.USD
                binding.txtMarketCapDetail.text =
                    "$" + coinMarketsList.marketData!!.marketCap!!["usd"]
                binding.txtMarketCapRankDetail.text =
                    "Market cap #" + coinMarketsList.marketCapRank.toString()


                binding.txtMarketAverageDetail.text =
                    applicationContext.getString(R.string.market_average)
                chart(
                    getData(
                        coinGecko!!.getCoinMarketChartRangeById(
                            id,
                            Currency.USD,
                            (Utils.getDaysAgo(2) / 1000L).toString(),
                            (System.currentTimeMillis() / 1000L).toString()
                        )
                    )
                )
                binding.mpChart.visibility = View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            Toast.makeText(
                applicationContext,
                resources.getString(R.string.internetNotAvailable),
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    private fun chart(data: LineData) {


        var yAxis: YAxis = binding.mpChart.axisRight
        yAxis.isEnabled = false
//
        yAxis = binding.mpChart.axisLeft
        yAxis.textColor = Color.WHITE
        val xAxis: XAxis = binding.mpChart.xAxis
        xAxis.isEnabled = false

        // no description text
        binding.mpChart.description.isEnabled = false

        // enable touch gestures
        binding.mpChart.setTouchEnabled(true)


        // enable scaling and dragging
        binding.mpChart.isDragEnabled = true
        binding.mpChart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately

        // if disabled, scaling can be done on x- and y-axis separately
        binding.mpChart.setPinchZoom(true)

        // add data
        binding.mpChart.data = data

        // get the legend (only possible after setting data)

        binding.mpChart.description.textColor = Color.GREEN
        // animate calls invalidate()...

        // animate calls invalidate()...
        binding.mpChart.animateX(1500)

        // don't forget to refresh the drawing

        binding.mpChart.invalidate()

//        binding.time.isEnabled = true
    }

    private fun getData(coinMarketChartById: MarketChart): LineData {
        val dataVals = ArrayList<Entry>()
        for (m in coinMarketChartById.prices) {
            dataVals.add(Entry(m[0].toFloat(), m[1].toFloat()))

        }


        // create a dataset and give it a type
        val set1 = LineDataSet(dataVals, "DataSet 1")


        val drawable = ContextCompat.getDrawable(this, R.drawable.chart_gradient)
        set1.fillDrawable = drawable
        set1.setDrawFilled(true)

        set1.setDrawValues(false)


        // create a data object with the data sets
        return LineData(set1)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out_new)
    }
}