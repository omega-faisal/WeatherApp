package com.example.weatherapp


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityHistory2Binding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
   private var bindingHistory:ActivityHistory2Binding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHistory=ActivityHistory2Binding.inflate(layoutInflater)

        setContentView(bindingHistory?.root)

        //setting up the history dao and passing it to the function getALLDates
        val dao=(application as HistoryApplication).db.historyDao()
        getAllNames(dao)


        bindingHistory?.buttonSubmit?.setOnClickListener {
            val cityText=bindingHistory?.etCurrentCity?.text
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra(Constants.cityname,cityText)
            startActivity(intent)
            finish()
        }


    }
    private fun getAllNames(historyDao: HistoryDao)
    {

        lifecycleScope.launch{
            historyDao.fetchAllNames().collect(){AllCityNames->

                if(AllCityNames.isNotEmpty())
                {
                    bindingHistory?.tvHistory?.visibility= View.VISIBLE
                    bindingHistory?.rvHistory?.visibility=View.VISIBLE
                    bindingHistory?.tvNoDataAvailable?.visibility=View.INVISIBLE

                    bindingHistory?.rvHistory?.layoutManager=LinearLayoutManager(this@HistoryActivity)

                    val cityNames=ArrayList<String>()
                    for(city in AllCityNames)
                    {
                        cityNames.add(city.cityName)
                    }
                    val historyAdapter=HistoryAdapter(cityNames)

                    bindingHistory?.rvHistory?.adapter=historyAdapter

                }
                else{
                    bindingHistory?.tvHistory?.visibility= View.INVISIBLE
                    bindingHistory?.rvHistory?.visibility=View.INVISIBLE
                    bindingHistory?.tvNoDataAvailable?.visibility=View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingHistory=null
    }
}