package com.z100.geopal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.z100.geopal.R
import com.z100.geopal.entity.Reminder

class RemindersViewAdapter(private val rvData: List<Reminder>) :
    RecyclerView.Adapter<RemindersViewAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val listItem: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_reminder, parent, false)

        return ItemViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val reminder: Reminder = rvData[position]

        holder.tvTitle.text = reminder.description
        holder.tvLocation.text = reminder.title
        holder.linearLayout.setOnLongClickListener {
            holder.btnDeleteReminder.isVisible = !holder.btnDeleteReminder.isVisible
            true
        }

        holder.linearLayout.setOnClickListener { view ->
            Toast.makeText(view.context, reminder.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return rvData.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_reminder_title)
        var tvLocation: TextView = itemView.findViewById(R.id.tv_reminder_location)
        var btnDeleteReminder: ImageView = itemView.findViewById(R.id.btn_delete_reminder)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.ll_reminder_layout)
    }
}
