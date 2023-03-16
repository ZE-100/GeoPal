package com.z100.geopal.database.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.z100.geopal.database.contracts.LocationContract
import com.z100.geopal.database.contracts.ReminderContract.SQL_CREATE_REMINDER_TABLE
import com.z100.geopal.database.contracts.ReminderContract.SQL_DELETE_REMINDER_TABLE
import com.z100.geopal.database.contracts.LocationContract.SQL_CREATE_LOCATION_TABLE
import com.z100.geopal.database.contracts.LocationContract.SQL_DELETE_LOCATION_TABLE
import com.z100.geopal.database.contracts.NetworkContract
import com.z100.geopal.database.contracts.NetworkContract.SQL_CREATE_NETWORK_TABLE
import com.z100.geopal.database.contracts.NetworkContract.SQL_DELETE_NETWORK_TABLE
import com.z100.geopal.database.contracts.ReminderContract
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.Network
import com.z100.geopal.pojo.Reminder

class ReminderDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Reminder.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_REMINDER_TABLE)
        db.execSQL(SQL_CREATE_LOCATION_TABLE)
        db.execSQL(SQL_CREATE_NETWORK_TABLE)
    }

    fun deleteAll() {
        writableDatabase.delete(ReminderContract.ReminderEntry.TABLE_NAME, null, null)
        writableDatabase.delete(LocationContract.LocationEntry.TABLE_NAME, null, null)
        writableDatabase.delete(NetworkContract.NetworkEntry.TABLE_NAME, null, null)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_REMINDER_TABLE)
        db.execSQL(SQL_DELETE_LOCATION_TABLE)
        db.execSQL(SQL_DELETE_NETWORK_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertReminder(reminder: Reminder) {
        var locationId: Long? = null
        if (reminder.location != null) {
            val locationValues = ContentValues().apply {
                put(LocationContract.LocationEntry.COLUMN_NAME, reminder.location.name)
                put(LocationContract.LocationEntry.COLUMN_LAT, reminder.location.lat)
                put(LocationContract.LocationEntry.COLUMN_LON, reminder.location.lon)
            }
            locationId = writableDatabase.insertOrThrow(LocationContract.LocationEntry.TABLE_NAME, null, locationValues)
        }

        var networkId: Long? = null
        if (reminder.network != null) {
            val networkValues = ContentValues().apply {
                put(NetworkContract.NetworkEntry.COLUMN_NAME, reminder.network.name)
                put(NetworkContract.NetworkEntry.COLUMN_SSID, reminder.network.ssid)
            }
            networkId = writableDatabase.insertOrThrow(NetworkContract.NetworkEntry.TABLE_NAME, null, networkValues)
        }

        val reminderValues = ContentValues().apply {
            put(ReminderContract.ReminderEntry.COLUMN_TITLE, reminder.title)
            put(ReminderContract.ReminderEntry.COLUMN_DESCRIPTION, reminder.description)
            put(ReminderContract.ReminderEntry.COLUMN_LOCATION, locationId)
            put(ReminderContract.ReminderEntry.COLUMN_NETWORK, networkId)
        }

        writableDatabase.insertOrThrow("reminder", null, reminderValues)
    }

    fun findAllReminders(): List<Reminder> {
        val cursor = readableDatabase.query(ReminderContract.ReminderEntry.TABLE_NAME,null, null, null, null, null, null)
        return mapToReminders(cursor)
    }

    fun findLocation(id: Long): Location? {
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = readableDatabase.query(LocationContract.LocationEntry.TABLE_NAME,null, selection, selectionArgs, null, null, null)
        return mapLocation(cursor)
    }

    fun findNetwork(id: Long): Network? {
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = readableDatabase.query(NetworkContract.NetworkEntry.TABLE_NAME,null, selection, selectionArgs, null, null, null)
        return mapNetwork(cursor)
    }

    private fun mapToReminders(cursor: Cursor): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        with(cursor) {
            while (moveToNext()) {
                val reminder = Reminder(
                    getString(getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_TITLE)),
                    getString(getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_DESCRIPTION)),
                    findLocation(getLong(getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_LOCATION))),
                    findNetwork(getLong(getColumnIndexOrThrow(ReminderContract.ReminderEntry.COLUMN_NETWORK)))
                )
                reminders.add(reminder)
            }
        }
        return reminders
    }

    private fun mapLocation(cursor: Cursor): Location? {
        with(cursor) {
            while (moveToNext()) {
                return Location(
                    getString(getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME)),
                    getDouble(getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LAT)),
                    getDouble(getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LON))
                )
            }
        }
        return null
    }

    private fun mapNetwork(cursor: Cursor): Network? {
        with(cursor) {
            while (moveToNext()) {
                return Network(
                    getString(getColumnIndexOrThrow(NetworkContract.NetworkEntry.COLUMN_NAME)),
                    getString(getColumnIndexOrThrow(NetworkContract.NetworkEntry.COLUMN_SSID))
                )
            }
        }
        return null
    }
}
