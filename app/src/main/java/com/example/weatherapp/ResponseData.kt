package com.example.weatherapp

data class ResponseData(
    val wind:wind,
    val main:main,
    val clouds:clouds,
    val timezone:Float,
    val name:String,
    val weather:List<weatherList>,
    val dt:Long

)

data class main(
    val temp:Float,
    val feels_like:Float,
    val temp_min:Float,
    val temp_max:Float,
    val pressure:Float,
    val humidity:Float
)


data class wind(
    val speed:Float,
    val deg:Float
)
data class clouds(
    val all:Float
)

data class weatherList(
    val id:Int,
    val main:String,
    val description:String,
    val icon:String
)




