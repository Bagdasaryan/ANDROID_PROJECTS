package com.example.reminder.RECYCLER_NOTIFICATIONS

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.reminder.R
import com.example.reminder.isDateExpired
import kotlinx.android.synthetic.main.activity_main.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AdapterRecyclerViewNotes(var items: List<ModelRecyclerViewNotifications>, val callback: Callback): RecyclerView.Adapter<AdapterRecyclerViewNotes.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_notes_recycler_view, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.idTitle)
        private val description = itemView.findViewById<TextView>(R.id.idDescription)
        private val time = itemView.findViewById<TextView>(R.id.idTime)

        @SuppressLint("SetTextI18n")
        fun bind(item: ModelRecyclerViewNotifications) {
            //Add RECTANGLE
            title.text = item.title
            description.text = item.description

            //
            var hourText = item.hour.toString()
            var minuteText = item.minute.toString()
            if(item.hour < 10) {
                hourText = "0$hourText"
            }
            if(item.minute < 10) {
                minuteText = "0$minuteText"
            }
            time.text = "${item.day}.${item.month+1}.${item.year} ${hourText}:${minuteText}"

            // Is date expired
            var color = 0
            color = if(isDateExpired(item.day, item.month, item.year, item.hour, item.minute)) {
                Color.rgb(216,216,216)
            } else {
                Color.rgb(0, 0, 0)
            }
            title.setTextColor(color)
            description.setTextColor(color)
            time.setTextColor(color)

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ModelRecyclerViewNotifications)
    }
}
