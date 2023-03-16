package com.z100.geopal.service

import android.content.ContentValues
import android.content.Context
import com.z100.geopal.database.contracts.LocationContract
import com.z100.geopal.database.contracts.NetworkContract
import com.z100.geopal.database.contracts.ReminderContract
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.Network
import com.z100.geopal.pojo.Reminder

class TestDataService(private val context: Context) {

    private val dbHelper = ReminderDBHelper(context)

    fun addTestRemindersToDB() {
        provideRemindersList().forEach {
            ReminderDBHelper(context).insertReminder(it)
        }
//        db.close() //Thierry was here TODO: Add back in
    }

    fun removeTestRemindersFromDB() {
        val db = dbHelper.writableDatabase

        // Define 'where' part of query.
        val selection = "${ReminderContract.ReminderEntry.COLUMN_DESCRIPTION} LIKE ?"

        provideRemindersList().forEach {
            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(it.description)

            // Issue SQL statement.
            val deletedRows = db.delete(ReminderContract.ReminderEntry.TABLE_NAME, selection, selectionArgs)
        }
    }

    fun provideRemindersList(): List<Reminder> {
        return listOf(
            Reminder(null, "Buy bread at store", Location("Paris", 42.23123, 41.23123), null),
            Reminder(null, "Get some sleep at Hotel", Location("Berlin", 43.51234, 42.12312), null),
            Reminder(null,  "Start working on project", null, Network("Starbucks free", "SSID")),
            Reminder(null,  "Program JSbb++", null, Network("home-wifi-2", "SSID")),
            Reminder(null, "Buy a car", Location("Rome", 69.123123, 42.0123), null),
            Reminder(null, "Delete JSbb++", null, Network("home-wifi-1", "SSID")),
            Reminder(null, "Get some food", Location("Zurich", 44.4123, 43.2323), null)
        )
    }
}