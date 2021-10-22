package es.luissachaarancibiabazan.dak

import com.google.gson.annotations.SerializedName

data class WeatherResponse (@SerializedName("DailyForecasts") var DailyForecasts:String)

