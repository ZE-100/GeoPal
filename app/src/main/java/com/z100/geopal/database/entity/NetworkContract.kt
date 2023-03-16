package com.z100.geopal.database.entity

import android.provider.BaseColumns

object NetworkContract {
    object NetworkEntry : BaseColumns {
        const val TABLE_NAME = "network"
        const val COLUMN_NAME = "name"
        const val COLUMN_SSID = "ssid"
    }

    const val SQL_CREATE_NETWORK_TABLE = """
        CREATE TABLE ${NetworkEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${NetworkEntry.COLUMN_NAME} TEXT,
            ${NetworkEntry.COLUMN_SSID} TEXT
        );
    """

    const val SQL_DELETE_NETWORK_TABLE = "DROP TABLE IF EXISTS ${NetworkEntry.TABLE_NAME}"
}