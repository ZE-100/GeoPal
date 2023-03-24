package com.z100.geopal.service.api

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.z100.geopal.pojo.NominatimLocation
import java.lang.reflect.Type


/**
 * Generic approach based on [StringRequest]
 *
 * @author Z-100
 * @since 1.0
 */
class GsonRequest<T>(
    method: Int,
    url: String,
    clazz: Type,
    listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(method, url, errorListener) {

    private val mLock = Any()

    private var mClazzType = clazz

    private var mListener: Response.Listener<T>? = listener

    private var mParams: MutableMap<String, String>? = null

    private var mBody: String? = null

    private var mHeaders: MutableMap<String, String>? = null

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {

        val parsed: T = try {
            Gson().fromJson(String(response!!.data), mClazzType)
        } catch (e: Exception) {
            val errorMsg = "Could not parse object: ${response?.data?.let { String(it) }}"
            Log.e("GsonRequest", errorMsg)
            return Response.error(VolleyError(errorMsg))
        }
        Log.d("GsonRequest", "Network response: ${String(response.data)}")

        return Response.success(parsed, null)
    }

    private fun getList(jsonArray: String, clazz: Class<T>): List<T>? {
        val typeOfT: Type = TypeToken.getParameterized(MutableList::class.java, clazz).type
        return Gson().fromJson(jsonArray, typeOfT)
    }

    override fun deliverResponse(response: T?) {
        var kListener: Response.Listener<T>?
        synchronized(mLock) {
            kListener = mListener
        }
        kListener?.onResponse(response)
    }

    override fun cancel() {
        super.cancel()
        synchronized(mLock) {
            mListener = null
        }
    }

    fun withParam(name: String, value: String): GsonRequest<T> {
        if (mParams == null)
            mParams = HashMap()
        mParams!![name] = value
        return this
    }

    override fun getParams(): MutableMap<String, String>? {
        return mParams?:super.getParams()
    }

    fun withBody(body: Any): GsonRequest<T> {
        mBody = Gson().toJson(body)
        return this
    }

    override fun getBody(): ByteArray {
        return mBody?.toByteArray() ?: super.getBody()
    }

    fun withHeader(key: String, value: String): GsonRequest<T> {
        if (mHeaders == null)
            mHeaders = HashMap()
        mHeaders?.put(key, value)
        return this
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mHeaders ?: super.getHeaders()
    }
}
