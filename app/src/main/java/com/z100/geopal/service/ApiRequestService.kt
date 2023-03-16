package com.z100.geopal.service

import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.StringRequest
import com.z100.geopal.MainActivity
import com.z100.geopal.util.Callback
import com.z100.geopal.util.Globals.Factory.API_NOMINATIM_REQUEST

class ApiRequestService {

    fun getLocationSearchResults(location: String, callback: Callback<String>) {
        val req = StringRequest(GET, "$API_NOMINATIM_REQUEST$location",
            { res ->
                callback.handle(res, null)
            }, { err ->
                callback.handle(null, err)
            })
        MainActivity.requestQueue.add(req)
    }
}