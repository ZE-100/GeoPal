package com.z100.geopal.service

import android.content.SharedPreferences
import com.z100.geopal.util.Globals.Factory.SETTINGS_TEST_MODE_TOGGLE

class SPDataService(private val sp: SharedPreferences) {

    private val editor = sp.edit()

    fun switchTestMode() = editor.apply { putBoolean(SETTINGS_TEST_MODE_TOGGLE, !getSwitchTestMode()) }.commit()

    fun getSwitchTestMode(): Boolean = sp.getBoolean(SETTINGS_TEST_MODE_TOGGLE, false)
}