package com.z100.geopal.util

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets.UTF_8

class GsonRequest<Clazz>(
    method: Int,
    url: String,
    clazz: Class<*>,
    listener: Response.Listener<Clazz>,
    errorListener: Response.ErrorListener
) : Request<Clazz>(method, url, errorListener) {

    private val mLock = Any()

    private val mClazz: Class<*> = clazz

    private var mListener: Response.Listener<Clazz>? = listener

    private var mParams: MutableMap<String, String>? = null

    private var mBody: String? = null

    private var mHeaders: MutableMap<String, String>? = null

    override fun parseNetworkResponse(response: NetworkResponse?): Response<Clazz> {

        print("Network response: ${String(response!!.data, UTF_8)}")

        val parsed = try {
            Gson().fromJson(String(response.data, UTF_8), mClazz) as Clazz
        } catch (e: Exception) {
            throw RuntimeException("Could not parse object: " + (response.data?.toString() ?: "[data null]"), e)
        }
        return Response.success(parsed, null)
    }

    override fun deliverResponse(response: Clazz?) {
        var kListener: Response.Listener<Clazz>?
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

    fun withParam(name: String, value: String): GsonRequest<Clazz> {
        if (mParams == null)
            mParams = HashMap()
        mParams!![name] = value
        return this
    }

    override fun getParams(): MutableMap<String, String>? {
        return mParams?:super.getParams()
    }

    fun withBody(body: Any): GsonRequest<Clazz> {
        mBody = Gson().toJson(body)
        return this
    }

    override fun getBody(): ByteArray {
        return mBody?.toByteArray() ?: super.getBody()
    }

    fun withHeader(key: String, value: String): GsonRequest<Clazz> {
        if (mHeaders == null)
            mHeaders = HashMap()
        mHeaders?.put(key, value)
        return this
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mHeaders ?: super.getHeaders()
    }
}