package com.example.weatherapp

import android.app.Application

class HistoryApplication:Application() {
    val db by lazy{
        HistoryDatabase.getInstance(this)
    }

}