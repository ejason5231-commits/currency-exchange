import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.currencyexchange.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CalculatorFragment : Fragment() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var tvResult: TextView

    private var rates: Map<String, Double> = emptyMap()
    private val currencies = listOf("USD", "EUR", "GBP", "JPY", "MMK")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        spinnerFrom = view.findViewById(R.id.spinner_from_currency)
        spinnerTo = view.findViewById(R.id.spinner_to_currency)
        etFrom = view.findViewById(R.id.et_from_amount)
        etTo = view.findViewById(R.id.et_to_amount)
        tvResult = view.findViewById(R.id.tv_conversion_result)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(0) // USD
        spinnerTo.setSelection(4) // MMK

        fetchRates()

        etFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { convert(true) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etTo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { convert(false) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { convert(true) }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { convert(true) }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    private fun fetchRates() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val call = api.getRates("YOUR_API_KEY", "USD", currencies.joinToString(","))

        call.enqueue(object : Callback<RateResponse> {
            override fun onResponse(call: Call<RateResponse>, response: Response<RateResponse>) {
                if (response.isSuccessful) {
                    rates = response.body()?.quotes ?: emptyMap()
                    convert(true)
                }
            }

            override fun onFailure(call: Call<RateResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun convert(fromToTo: Boolean) {
        val fromCurrency = currencies[spinnerFrom.selectedItemPosition]
        val toCurrency = currencies[spinnerTo.selectedItemPosition]
        val fromKey = "USD$fromCurrency"
        val toKey = "USD$toCurrency"

        val fromRate = if (fromCurrency == "USD") 1.0 else rates[fromKey] ?: 0.0
        val toRate = if (toCurrency == "USD") 1.0 else rates[toKey] ?: 0.0

        if (fromRate == 0.0 || toRate == 0.0) return

        if (fromToTo) {
            val amount = etFrom.text.toString().toDoubleOrNull() ?: 0.0
            val converted = amount * (toRate / fromRate)
            etTo.setText(converted.toString())
            tvResult.text = "$amount $fromCurrency = $converted $toCurrency"
        } else {
            val amount = etTo.text.toString().toDoubleOrNull() ?: 0.0
            val converted = amount * (fromRate / toRate)
            etFrom.setText(converted.toString())
            tvResult.text = "$converted $fromCurrency = $amount $toCurrency"
        }
    }
}
