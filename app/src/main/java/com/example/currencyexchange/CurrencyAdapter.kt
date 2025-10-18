import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.R

class CurrencyAdapter : ListAdapter<Currency, CurrencyAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flag: ImageView = view.findViewById(R.id.iv_currency_flag)
        val code: TextView = view.findViewById(R.id.tv_currency_code)
        val value: TextView = view.findViewById(R.id.tv_currency_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exchange_rate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = getItem(position)
        holder.code.text = currency.code
        holder.value.text = "1 ${currency.code} = ${currency.rate} MMK"
        // holder.flag.setImageResource(resources.getIdentifier(currency.flagRes, "drawable", context.packageName))
    }

    class DiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem.code == newItem.code
        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem == newItem
    }
}
