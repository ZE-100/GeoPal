package com.z100.geopal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.z100.geopal.R
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Reminder

class RemindersViewAdapter(private val context: Context, private val rvData: List<Reminder>) :
    RecyclerView.Adapter<RemindersViewAdapter.ItemViewHolder>() {

    private fun getDrawable(reminder: Reminder): Int =
        reminder.location?.drawable ?: reminder.network!!.drawable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val listItem: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_reminder, parent, false)

        return ItemViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val reminder: Reminder = rvData[position]

        holder.ivIcon.setImageDrawable(context.getDrawable(getDrawable(reminder)))
        holder.tvTitle.text = reminder.description
        holder.tvLocation.text = if (reminder.location == null) reminder.network!!.name else reminder.location.name

        holder.linearLayout.setOnLongClickListener {
            holder.btnDeleteReminder.isVisible = !holder.btnDeleteReminder.isVisible
            holder.isDeletable = !holder.isDeletable
            true
        }

        holder.linearLayout.setOnClickListener {
            if (holder.isDeletable) {
//                ReminderDBHelper(context).deleteByUUID(reminder.uuid) //TODO
            }
        }
    }

    override fun getItemCount(): Int {
        return rvData.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivIcon: ImageView = itemView.findViewById(R.id.iv_reminder_icon)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_reminder_title)
        var tvLocation: TextView = itemView.findViewById(R.id.tv_reminder_location)
        var btnDeleteReminder: ImageView = itemView.findViewById(R.id.btn_delete_reminder)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.ll_reminder_layout)
        var isDeletable = false
    }
}
