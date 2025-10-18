import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.currencyexchange.R
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class GoldPricesFragment : Fragment() {

    private lateinit var tvWorldGold: TextView
    private lateinit var tv16Pae: TextView
    private lateinit var tv15Pae: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gold_prices, container, false)

        tvWorldGold = view.findViewById(R.id.tv_world_gold_price)
        tv16Pae = view.findViewById(R.id.tv_16_pae_yay)
        tv15Pae = view.findViewById(R.id.tv_15_pae_yay)

        fetchWorldGold()
        fetchMyanmarGold()

        return view
    }

    private fun fetchWorldGold() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.goldapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GoldApiService::class.java)
        val call = api.getGoldPrice("YOUR_GOLD_API_KEY", "USD")

        call.enqueue(object : Callback<GoldResponse> {
            override fun onResponse(call: Call<GoldResponse>, response: Response<GoldResponse>) {
                if (response.isSuccessful) {
                    val price = response.body()?.price ?: 0.0
                    tvWorldGold.text = "World Gold Price: $price USD/oz"
                }
            }

            override fun onFailure(call: Call<GoldResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun fetchMyanmarGold() {
        thread {
            try {
                val doc = Jsoup.connect("https://hellolinker.net/rates/gold-price").get()
                // Parse the document to extract prices
                // Assuming selectors based on structure, adjust accordingly
                val sixteenPae = doc.select("selector_for_16_pae").text() // e.g., "9,104,757.66 Ks"
                val fifteenPae = doc.select("selector_for_15_pae").text()

                activity?.runOnUiThread {
                    tv16Pae.text = "16 Pae Yay: $sixteenPae MMK"
                    tv15Pae.text = "15 Pae Yay: $fifteenPae MMK"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

interface GoldApiService {
    @GET("XAU/{currency}")
    fun getGoldPrice(
        @Header("x-access-token") token: String,
        @Path("currency") currency: String
    ): Call<GoldResponse>
}

data class GoldResponse(
    val price: Double
    // Add other fields as per API
)
