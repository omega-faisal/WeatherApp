package com.example.weatherapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.HistoryRowLayoutBinding


class HistoryAdapter(private val items:ArrayList<String>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(binding: HistoryRowLayoutBinding):RecyclerView.ViewHolder(binding.root)
    {
        val llHistoryItemMain=binding.llHistoryItemMain
        val tvItem =binding.tvItem
        val tvPosition=binding.tvPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val city:String= items[position]

        holder.tvPosition.text=(position +1).toString()
        holder.tvItem.text=city
        //todo we will have to change this by the name of city which we will get from the main activity

        if(position%2==0)
        {
            holder.llHistoryItemMain.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context,
                R.color.lightGray)
            )
            // method for getting color stored in the colors file
        }
        else
        {
            holder.llHistoryItemMain.setBackgroundColor(
                Color.parseColor("#FFFFFF")
            )
            // method for hardcoding the color string
        }
    }

    override fun getItemCount(): Int {
      return items.size
    }
}