package com.z100.geopal.util

import android.util.Log

/**
 * Logger to log stuff to the logs and retrieve
 * the parameterized log string
 *
 * @author Z-100
 * @since 2.0
 */
class Logger {

    companion object Factory {

        /**
         * Function to log a string with or without parameters to the debug-log
         *
         * Parameters will be set to the message, by replacing "{}" in the
         * message itself.
         * Provide a to-be-parameterized function call as follows:
         * log(..., "Me {} age {}", "param1", "param2")
         *
         * @param caller The caller of the function, usually "this.javaClass"
         * @param message The to-be-logged message
         * @param params The optional parameters
         */
        fun log(caller: Class<*>, message: String, vararg params: String): String {
            return log(LogMode.DEBUG, caller, message, *params)
        }

        /**
         * Function to log a string with or without parameters
         *
         * Parameters will be set to the message, by replacing "{}" in the
         * message itself.
         * Provide a to-be-parameterized function call as follows:
         * log(..., "Me {} age {}", "param1", "param2")
         *
         * @param logMode The debug mode see [LogMode]
         * @param caller The caller of the function, usually "this.javaClass"
         * @param message The to-be-logged message
         * @param params The optional parameters
         */
        fun log(logMode: LogMode, caller: Class<*>, message: String, vararg params: String): String {

            val callerS = caller.toString()
            var finalMessage = message

            params.forEach { finalMessage = finalMessage.replaceFirst("{}", it) }

            when (logMode) {
                LogMode.INFO -> Log.i(callerS, message)
                LogMode.DEBUG -> Log.d(callerS, message)
                LogMode.ERROR -> Log.e(callerS, message)
                LogMode.TEST -> println("$callerS, $message")
            }

            return message
        }
    }

    /**
     * Mode which determines the log location
     */
    enum class LogMode {
        INFO, DEBUG, ERROR, TEST
    }
}