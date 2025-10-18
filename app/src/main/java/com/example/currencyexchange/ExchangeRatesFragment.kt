import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExchangeRatesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CurrencyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exchange_rates, container, false)

        recyclerView = view.findViewById(R.id.rv_exchange_rates)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CurrencyAdapter()
        recyclerView.adapter = adapter

        fetchExchangeRates()

        return view
    }

    private fun fetchExchangeRates() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val call = api.getRates("YOUR_API_KEY", "USD", "MMK,EUR,GBP,JPY") // Add more currencies

        call.enqueue(object : Callback<RateResponse> {
            override fun onResponse(call: Call<RateResponse>, response: Response<RateResponse>) {
                if (response.isSuccessful) {
                    val rates = response.body()?.quotes ?: emptyMap()
                    val currencyList = rates.map { (key, value) -> Currency(key.substring(3), value, "flag_$key") } // Assume flag drawables
                    adapter.submitList(currencyList)
                }
            }

            override fun onFailure(call: Call<RateResponse>, t: Throwable) {
                // Handle error
            }
        })
    }
}

data class Currency(val code: String, val rate: Double, val flagRes: String)

interface ApiService {
    @GET("latest")
    fun getRates(
        @Query("access_key") accessKey: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): Call<RateResponse>
}

data class RateResponse(
    val success: Boolean,
    val quotes: Map<String, Double>
)
