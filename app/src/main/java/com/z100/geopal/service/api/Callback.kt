package com.z100.geopal.service.api

import com.android.volley.VolleyError

/**
 * Callback used in the [ApiRequestService] to
 * store the received data or errors temporarily
 * in a function supplied by the called.
 *
 * @author Z-100
 * @since 1.0
 */
fun interface Callback<T> {
    fun handle(success: T?, error: VolleyError?)
}
