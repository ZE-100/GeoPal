package com.z100.geopal.util

import com.android.volley.VolleyError

fun interface Callback<T> {
    fun handle(success: T?, error: VolleyError?)
}