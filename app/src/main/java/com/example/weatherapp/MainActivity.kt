package com.example.weatherapp

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.loader.content.AsyncTaskLoader
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    val Api: String = "e7ca6ee7483b3f492a62b909b96bbd0f"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val etCityName=intent.getStringExtra(Constants.cityname)

        //creating the dao and then passing it to the function
        val dao=(application as HistoryApplication).db.historyDao()


        if(etCityName!=null) {
            val city = etCityName.toString()
            CallApiLoginAsyncTask(city, Api,dao).execute()
        }
        else
        {
            Log.e("error","Switching was unsuccessful")
        }


    }

    private inner class CallApiLoginAsyncTask(var city:String,var Api:String,var dao:HistoryDao): AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

            ShowProgressDialog()

            binding?.mainContainer?.visibility=View.GONE
        }

        lateinit var customprogressBar: Dialog

        // the main functioning has been done here
        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null

            try {
                val url = URL("https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${Api}")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true // this is for getting an input from the site
                connection.doOutput = true // this is for sending data to the site


                //TODO-this is code for receiving information
                val httpResult: Int = connection.responseCode
                // this will store the response code which is basically the remarks of process
                // as 200 stands for ok. its similar to status code
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(
                        InputStreamReader(inputStream)
                    )
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        while (reader.readLine().also { line = it } != null) {
                            // here we are checking if the reader still has a line to read
                            // and also gives the result of readline as it and we set that result to our line and
                            // check if its null or not

                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                    // we are setting result to the data stringbuilder extracted from site
                } else {
                    result = connection.responseMessage
                }
            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error: " + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            CancelProgressDialog()
            Log.i("JSON RESPONSE RESULT:", result)

            binding?.mainContainer?.visibility=View.VISIBLE


            //Todo- now we will use Gson to do the above mentioned processes
            val responseData = Gson().fromJson(result, ResponseData::class.java)
            // here we are using gson and setting our Response data class as our base model


            val newWeather=responseData.weather[0]
            // newWeather will store weather object which is at index 0
            binding?.tvStatus?.text=newWeather.description.capitalize()

            val tempInC=(responseData.main.temp-273).toInt()
            binding?.tvTemp?.text=tempInC.toString()+"°C"

            val MintempInC=(responseData.main.temp_min-273).toInt()
            binding?.tvMinTemp?.text="Min "+MintempInC.toString()+"°C"

            val MaxtempInC=(responseData.main.temp_max-273).toInt()
            binding?.tvMaxTemp?.text="Max "+MaxtempInC.toString()+"°C"

            val FeelsLike=(responseData.main.feels_like-273).toInt()
            binding?.tvFeelsLike?.text="Feels Like "+FeelsLike.toString()+"°C"


            binding?.tvHumidity?.text=responseData.main.humidity.toInt().toString()+" %"

            binding?.tvClouds?.text=responseData.clouds.all.toString()+" %"

            binding?.tvWindSpeed?.text=responseData.wind.speed.toInt().toString()+" km/h"

            val updateAt=responseData.dt
            val updateAtText="Updated at: "+SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(updateAt)

            binding?.tvUpdateTime?.text=updateAtText

            binding?.tvCurrentCity?.text=responseData.name

            addDateToDatabase(dao,city)

        }
        private fun ShowProgressDialog()
        {
            customprogressBar= Dialog(this@MainActivity)
            customprogressBar.setContentView(R.layout.progrssdialog_custom)
            customprogressBar.show()
        }

        private fun CancelProgressDialog()
        {
            customprogressBar.dismiss()
        }
    }

    private fun addDateToDatabase(historyDao: HistoryDao,cityname:String){
        lifecycleScope.launch {
            historyDao.InsertCityName(HistoryEntity(cityname))
            Log.e("date:",
                "added")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}