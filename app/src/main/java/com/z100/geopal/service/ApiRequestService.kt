package com.z100.geopal.service

import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.StringRequest
import com.google.gson.reflect.TypeToken
import com.z100.geopal.MainActivity
import com.z100.geopal.pojo.NominatimLocationDTO
import com.z100.geopal.util.Callback
import com.z100.geopal.util.Globals.Factory.API_NOMINATIM_REQUEST
import com.z100.geopal.util.GsonRequest

class ApiRequestService {

    fun getLocationSearchResults(location: String, callback: Callback<List<NominatimLocationDTO>>) {
        val req = GsonRequest<List<NominatimLocationDTO>>(GET, "$API_NOMINATIM_REQUEST$location",
            object : TypeToken<List<NominatimLocationDTO>?>() {}.rawType,
            { res ->
                callback.handle(res, null)
            }, { err ->
                callback.handle(null, err)
            })
        MainActivity.requestQueue.add(req)
    }
}