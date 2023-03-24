package com.z100.geopal.service.data.types

import kotlin.reflect.KClass
import com.z100.geopal.service.data.SPDataService

/**
 * This type is used to store the information needed by [SPDataService],
 * to save and get properties from the SharedPreferences.
 *
 * This type should only be used in the [com.z100.geopal.util.Globals] in
 * combination with the [SPDataService].
 *
 * Use this type as follows: `SPType("default-value", Boolean::class)`
 *
 * @property key the name of the property (e.g. default-value)
 * @property type the primitive type of the property (e.g. Boolean)
 *
 * @author Z-100
 * @since 2.0
 */
class SPType<T : Any>(val key: String, val type: KClass<T>)
