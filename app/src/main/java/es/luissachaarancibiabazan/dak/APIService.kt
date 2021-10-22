package es.luissachaarancibiabazan.dak

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    fun getWheatherByLocation(@Url url:String): Call<WeatherResponse>
}