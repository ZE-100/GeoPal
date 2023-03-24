package com.z100.geopal.service.api

import com.android.volley.Request.Method.GET
import com.google.gson.reflect.TypeToken
import com.z100.geopal.MainActivity
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
     * @param url the url for the to-be-sent request
     * @param callback used to handle the response
     * where its called
     *
     * @see [GsonRequest]
     */
    inline fun <reified T : Any> getRequest(url: String, callback: Callback<T>) {
        val req = GsonRequest<T>(GET, url, object : TypeToken<T>(){}.type,
            { res -> callback.handle(res, null) },
            { err -> callback.handle(null, err) }
        )
        MainActivity.requestQueue.add(req)
    }

    inline fun <reified T : Any> getLocationSearchResults(location: String, callback: Callback<T>) {
        getRequest("$API_NOMINATIM_REQUEST$location", callback)
    }
}
