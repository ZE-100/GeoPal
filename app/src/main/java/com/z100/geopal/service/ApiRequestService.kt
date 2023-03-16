package com.z100.geopal.service

import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonArray
import com.z100.geopal.MainActivity
import com.z100.geopal.pojo.NominatimLocationDTO
import com.z100.geopal.util.Callback
import com.z100.geopal.util.Globals.Factory.API_NOMINATIM_REQUEST
import com.z100.geopal.util.NominatimLocationRequest

class ApiRequestService {

    fun getLocationSearchResults(location: String, callback: Callback<List<NominatimLocationDTO>>) {
        val req = NominatimLocationRequest(GET, "$API_NOMINATIM_REQUEST$location",
            { res ->
                callback.handle(res, null)
            }, { err ->
                callback.handle(null, err)
            })
        MainActivity.requestQueue.add(req)
    }

    fun geshzt(location: String, callback: Callback<String>) {
        val req = StringRequest(GET, "$API_NOMINATIM_REQUEST$location",
            { res ->
                callback.handle(res, null)
            }, { err ->
                callback.handle(null, err)
            })
        MainActivity.requestQueue.add(req)
    }
}