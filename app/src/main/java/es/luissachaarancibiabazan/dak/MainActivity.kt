package es.luissachaarancibiabazan.dak

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    lateinit var txtClock:TextView
    lateinit var txtResponse:TextView
    lateinit var txtForecast:TextView
    lateinit var imageIcon:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getVolleyWheather()
        txtClock = findViewById<TextView>(R.id.txtClock)
        txtResponse = findViewById<TextView>(R.id.txtResponse)
        txtForecast = findViewById<TextView>(R.id.txtForecast)
        imageIcon = findViewById<ImageView>(R.id.forecastImage)

        val clock = findViewById<TextClock>(R.id.clock)

        clock.format24Hour = "HH:mm:ss"
        txtClock.text = txtClock.text

    }

    private fun getRetrofit(): Retrofit {
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client : OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        return Retrofit.Builder()
            .baseUrl("http://dataservice.accuweather.com/forecasts/v1/daily/1day/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getWheather(){
        doAsync {
            val location = 305619
            val apiKey = "BPqA6OBTh1C6ORoyQyr67ppa0BQWuRat"
            val call = getRetrofit().create(APIService::class.java).getWheatherByLocation("$location?apikey=$apiKey&language=es-es").execute()
            val wheather = call.body() as WeatherResponse
            uiThread {
                print(wheather)
            }
        }
    }

    fun getVolleyWheather(){
        val queue = Volley.newRequestQueue(this)
        val location = 305619
        val apiKey = "API-KEY"
        val url ="http://dataservice.accuweather.com/forecasts/v1/daily/1day/$location?apikey=$apiKey&language=es-es"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val data = response.getJSONArray("DailyForecasts")

                for (i in 0 until data.length()) {
                    val json = data.getJSONObject(i)
                    val temperatureJson = json.getJSONObject("Temperature")
                    val minimum = temperatureJson.getJSONObject("Minimum")

                    val temperature = (((minimum.getDouble("Value") - 32) * 5) / 9)

                    val icon = json.getJSONObject("Day").getString("Icon")
                    val urlIcon =
                        "https://developer.accuweather.com/sites/default/files/0$icon-s.png"
                    val iconPhrase = json.getJSONObject("Day").getString("IconPhrase")
                    Log.d("url", urlIcon)
                    val nameIcon = "ic_" + icon

                    val id = resources.getIdentifier("es.luissachaarancibiabazan:drawable"+ nameIcon, null,null)
                    Log.d("icon", nameIcon)
                    //imageIcon.setImageDrawable(resources.getIdentifier(nameIcon, "drawable",packageName))
                    imageIcon.setImageResource(resources.getIdentifier(nameIcon, "drawable",packageName))
                    imageIcon.display
                    txtResponse.text = round(temperature).toString() + " C"
                    txtForecast.text = iconPhrase
                }
                print(data)
            },
            { print("Ha ocurrido un error") }
        )
        queue.add(request)
    }
}
