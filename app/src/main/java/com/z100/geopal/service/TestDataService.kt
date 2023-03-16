package com.z100.geopal.service

import android.content.ContentValues
import android.content.Context
import com.z100.geopal.database.entity.LocationContract
import com.z100.geopal.database.entity.NetworkContract
import com.z100.geopal.database.entity.ReminderContract
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.Network
import com.z100.geopal.pojo.Reminder

class TestDataService(context: Context) {

    private val dbHelper = ReminderDBHelper(context)

    fun addTestRemindersToDB() {
        val db = dbHelper.writableDatabase

        provideRemindersList().forEach {

            var locationId: Long? = null
            if (it.location != null) {
                val locationValues = ContentValues().apply {
                    put(LocationContract.LocationEntry.COLUMN_NAME, it.location.name)
                    put(LocationContract.LocationEntry.COLUMN_LAT, it.location.lat)
                    put(LocationContract.LocationEntry.COLUMN_LON, it.location.lon)
                }

                locationId = db.insertOrThrow(LocationContract.LocationEntry.TABLE_NAME, null, locationValues)
            }

            var networkId: Long? = null
            if (it.network != null) {
                val networkValues = ContentValues().apply {
                    put(NetworkContract.NetworkEntry.COLUMN_NAME, it.network.name)
                    put(NetworkContract.NetworkEntry.COLUMN_SSID, it.network.ssid)
                }

                networkId = db.insertOrThrow(NetworkContract.NetworkEntry.TABLE_NAME, null, networkValues)
            }

            val reminderValues = ContentValues().apply {
                put(ReminderContract.ReminderEntry.COLUMN_TITLE, it.title)
                put(ReminderContract.ReminderEntry.COLUMN_DESCRIPTION, it.description)
                put(ReminderContract.ReminderEntry.COLUMN_LOCATION, locationId)
                put(ReminderContract.ReminderEntry.COLUMN_NETWORK, networkId)
            }

            db.insertOrThrow("reminder", null, reminderValues)
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