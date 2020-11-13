package com.example.covid_19

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.PARSING.parametersOfCountries


class AdapterRecyclerView(var items: List<parametersOfCountries>, val callback: Callback): RecyclerView.Adapter<AdapterRecyclerView.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_info, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val TVcharCode = itemView.findViewById<TextView>(R.id.TVcharCode)
//        private val TVinformation = itemView.findViewById<TextView>(R.id.TVinformation)
        private val tvCountry = itemView.findViewById<TextView>(R.id.idCountry)
        private val tvConfirmed = itemView.findViewById<TextView>(R.id.idConfirmed)
        private val tvDeaths = itemView.findViewById<TextView>(R.id.idDeaths)
        private val tvRecovered = itemView.findViewById<TextView>(R.id.idRecovered)

        fun bind(item: parametersOfCountries) {
//            TVcharCode.text = item.charCode
//            TVinformation.text = item.nominal.toString() + " " + item.name + " = " + item.value.toString() + " Рублей"
            tvCountry.text = item.country
            tvConfirmed.text = "Confirmed "+item.newConfirmed.toString()
            tvDeaths.text = "Deaths "+item.newDeaths.toString()
            tvRecovered.text = "Recovered "+item.newRecovered.toString()
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: parametersOfCountries)
    }
}
