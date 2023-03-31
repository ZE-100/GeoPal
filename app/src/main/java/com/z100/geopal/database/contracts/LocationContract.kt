package com.z100.geopal.database.contracts

import android.provider.BaseColumns

object LocationContract {
    object LocationEntry : BaseColumns {
        const val TABLE_NAME = "location"
        const val COLUMN_NAME = "name"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LON = "lon"
    }

    const val SQL_CREATE_LOCATION_TABLE = """
        CREATE TABLE ${LocationEntry.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${LocationEntry.COLUMN_NAME} TEXT,
            ${LocationEntry.COLUMN_LAT} REAL,
            ${LocationEntry.COLUMN_LON} REAL
        );
    """

    const val SQL_DELETE_LOCATION_TABLE = "DROP TABLE IF EXISTS ${LocationEntry.TABLE_NAME}"
}