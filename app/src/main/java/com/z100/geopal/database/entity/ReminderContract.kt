package com.z100.geopal.database.entity

import android.provider.BaseColumns

object ReminderContract {
    object ReminderEntry : BaseColumns {
        const val TABLE_NAME = "reminder"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_NETWORK = "network"
    }

    const val SQL_CREATE_REMINDER_TABLE = """
        CREATE TABLE ${ReminderEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${ReminderEntry.COLUMN_TITLE} TEXT,
            ${ReminderEntry.COLUMN_DESCRIPTION} TEXT,
            ${ReminderEntry.COLUMN_LOCATION} INTEGER,
            ${ReminderEntry.COLUMN_NETWORK} INTEGER,
            FOREIGN KEY(${ReminderEntry.COLUMN_LOCATION}) REFERENCES ${LocationContract.LocationEntry.TABLE_NAME}(${BaseColumns._ID}),
            FOREIGN KEY(${ReminderEntry.COLUMN_NETWORK}) REFERENCES ${NetworkContract.NetworkEntry.TABLE_NAME}(${BaseColumns._ID})
        );
    """

    const val SQL_DELETE_REMINDER_TABLE = "DROP TABLE IF EXISTS ${ReminderEntry.TABLE_NAME}"
}
