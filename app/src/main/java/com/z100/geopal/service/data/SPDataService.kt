package com.z100.geopal.service.data

import android.content.SharedPreferences
import com.z100.geopal.util.Globals.Factory.SP_SETTINGS_TEST_MODE_TOGGLE
import com.z100.geopal.service.data.types.SPType

/**
 * Service to save and gets various primitive types
 * into/from the shared preferences
 * The key-value system uses the [SPType]
 *
 * @author Z-100
 * @since 1.0
 * @see SPType
 */
class SPDataService(private val sp: SharedPreferences) {

    private val editor = sp.edit()

    /**
     * Function used to save any primitive-type
     * properties to the [SharedPreferences].
     *
     * @param spType the key-type pair, which here
     * is used to simply provide the key.
     * @param value the to be saved property
     *
     * @throws IllegalArgumentException Thrown on
     * non-primitive property supplied
     */
    fun <T : Any> put(spType: SPType<T>, value: Any) {
        editor.apply {
            when (value) {
                is String -> putString(spType.key, value)
                is Int -> putInt(spType.key, value)
                is Float -> putFloat(spType.key, value)
                is Long -> putLong(spType.key, value)
                is Boolean -> putBoolean(spType.key, value)
                else -> throw IllegalArgumentException("Unsupported type (${value.javaClass}) for key ${spType.key}")
            }
        }.commit()
    }

    /**
     * Function used to get any primitive-type
     * properties from the [SharedPreferences].
     *
     * @param spType the key-type pair, which here
     * is used to provide the key and the type of
     * the to-be-gotten value.
     *
     * @throws IllegalArgumentException Thrown on
     * non-primitive property supplied
     */
    fun <T : Any> get(spType: SPType<T>): T {
        return sp.let {
            when (spType.type) {
                String::class -> it.getString(spType.key, "") as T
                Int::class -> it.getInt(spType.key, 0) as T
                Float::class -> it.getFloat(spType.key, 0f) as T
                Long::class -> it.getLong(spType.key, 0L) as T
                Boolean::class -> it.getBoolean(spType.key, false) as T
                else -> throw IllegalArgumentException("Unsupported type (${spType.type}) for key ${spType.key}")
            }
        }
    }

    fun toggleTestMode() = put(SP_SETTINGS_TEST_MODE_TOGGLE, !get(SP_SETTINGS_TEST_MODE_TOGGLE))
}
