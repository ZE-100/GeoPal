package com.z100.geopal.util

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.Gson
import com.z100.geopal.pojo.NominatimLocationDTO
import java.nio.charset.StandardCharsets.UTF_8

class NominatimLocationRequest(
    method: Int,
    url: String,
    listener: Response.Listener<List<NominatimLocationDTO>>,
    errorListener: Response.ErrorListener
) : Request<List<NominatimLocationDTO>>(method, url, errorListener) {

    private val mLock = Any()

    private var mListener: Response.Listener<List<NominatimLocationDTO>>? = listener

    private var mParams: MutableMap<String, String>? = null

    private var mBody: String? = null

    private var mHeaders: MutableMap<String, String>? = null

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<NominatimLocationDTO>> {

        print("Network response: ${String(response!!.data, UTF_8)}")

        val parsed: List<NominatimLocationDTO> = try {
            Gson().fromJson<List<NominatimLocationDTO>>(String(response.data, UTF_8), List::class.java)
        } catch (e: Exception) {
            throw RuntimeException("Could not parse object: " + (response.data?.toString() ?: "[data null]"), e)
        }
        return Response.success(parsed, null)
    }

    override fun deliverResponse(response: List<NominatimLocationDTO>?) {
        var kListener: Response.Listener<List<NominatimLocationDTO>>?
        synchronized(mLock) {
            kListener = mListener
        }
        if (kListener != null) {
            kListener!!.onResponse(response)
        }
    }

    override fun cancel() {
        super.cancel()
        synchronized(mLock) {
            mListener = null
        }
    }

    fun withParam(name: String, value: String): NominatimLocationRequest {
        if (mParams == null)
            mParams = HashMap()
        mParams!![name] = value
        return this
    }

    override fun getParams(): MutableMap<String, String>? {
        return mParams?:super.getParams()
    }

    fun withBody(body: Any): NominatimLocationRequest {
        mBody = Gson().toJson(body)
        return this
    }

    override fun getBody(): ByteArray {
        return mBody?.toByteArray() ?: super.getBody()
    }

    fun withHeader(key: String, value: String): NominatimLocationRequest {
        if (mHeaders == null)
            mHeaders = HashMap()
        mHeaders?.put(key, value)
        return this
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mHeaders ?: super.getHeaders()
    }
}