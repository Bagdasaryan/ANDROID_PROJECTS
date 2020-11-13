package com.example.covid_19

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.PARSING.StatisticGet
import com.example.covid_19.PARSING.StatisticResponse
import com.example.covid_19.PARSING.parametersOfCountries
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getStat()
        startAlarmBroadcastReceiver(this)
    }

    private fun getStat() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(StatisticGet::class.java)
        val call = service.getCurrentStatistic()
        call.enqueue(object : Callback<StatisticResponse> {
            override fun onResponse(call: Call<StatisticResponse>, response: Response<StatisticResponse>) {
                if(response.code() == 200) {
                    val rateResponse = response.body()!!
                    val rate = rateResponse.countries!!

                    recyclerRead(rate)
                    for(i in 0..rate.size-1) {
                        Log.e("LOGS: ", rate[i].country + " || " + rate[i].newRecovered)
                    }
                } else {
                    Log.e("LOG IN IF: ", "error in if")
                }

            }

            override fun onFailure(call: Call<StatisticResponse>, t: Throwable) {
                Log.e("LOG IN FAILURE: ", "error in failure")
            }

        })
    }

    private fun startAlarmBroadcastReceiver(context: Context) {
        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent

        val intent: Intent = Intent(context, AlarmBroadcastReceiver::class.java)
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(), 60*1000,
            alarmIntent
        )
    }

    private fun recyclerRead(list: List<parametersOfCountries>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = AdapterRecyclerView(list, object: AdapterRecyclerView.Callback {
            override fun onItemClicked(item: parametersOfCountries) {
                // Some code...
            }
        })
        recyclerView.adapter = adapter
    }

}