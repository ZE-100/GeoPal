package com.z100.geopal.service.data

import android.content.Context
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.Network
import com.z100.geopal.pojo.Reminder

/**
 * Service to insert test data into the local
 * database. In this case: Different reminders
 *
 * @author Z-100
 * @since 1.0
 */
class TestDataService(context: Context) {

    private val dbHelper = ReminderDBHelper(context)

    fun addTestRemindersToDB() {
        testReminders().forEach { dbHelper.insertReminder(it) }
    }

    fun removeTestRemindersFromDB() {
        testReminders().forEach { dbHelper.deleteReminder(arrayOf(it.description)) }
    }

    private fun testReminders(): List<Reminder> {
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
