package com.z100.geopal.service.api

import android.util.Log
import android.widget.Toast
import com.android.volley.Request.Method.GET
import com.z100.geopal.activity.MainActivity
import com.z100.geopal.util.Globals.Factory.API_NOMINATIM_REQUEST

/**
 * Service to handle various requests to any
 * API.
 *
 * @author Z-100
 * @since 1.0
 * @see GsonRequest
 */
class ApiRequestService {

    /**
     * Handles any request and transforms it from
     * JSON to type [T]
     *
     * @param location the query-string for the
     * Nominatim location search
     * @param callback used to handle the response
     * where its called
     *
     * @see [GsonRequest]
     */
    inline fun <reified T : Any> getLocationSearchResults(location: String, callback: Callback<T>) {
        val req = GsonRequest(GET, "$API_NOMINATIM_REQUEST$location", T::class.java,
            { res -> callback.handle(res, null) },
            { err -> callback.handle(null, err) }
        )
        MainActivity.requestQueue.add(req)
    }
}
