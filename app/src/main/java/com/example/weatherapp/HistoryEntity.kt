package com.example.weatherapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history-table")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
     val id:String,

     val cityName:String
)
