/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lib.network.api

import com.google.gson.Gson
import retrofit2.Response
import timber.log.Timber

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<HopeResponse<T>>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                Timber.i("C_TAG response.code() ${if (body != null) Gson().toJson(body) else null}")
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else if (body.code != 100000 && body.code != 0) {
                    ApiErrorResponse(body.message ?: "unknown error", body.code ?: 0)
                } else if (body.`object` == null && body.list == null && body.rows == null) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = (body.`object` ?: body.list ?: body.rows)!!)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }

        fun <T, R> createWithTrans(response: Response<HopeResponse<R>>, transform : (R) -> T): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else if (body.code != 100000) {
                    ApiErrorResponse(body.message ?: "unknown error", body.code ?: 0)
                } else if (body.`object` == null && body.list == null && body.rows == null) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = transform(body.`object` ?: body.list ?: body.rows!!))
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>(){
    override fun toString(): String {
        return "ApiEmptyResponse()"
    }
}

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>(){
    override fun toString(): String {
        return "ApiSuccessResponse(body=$body)"
    }
}

data class ApiErrorResponse<T>(val errorMessage: String, val errorCode: Int = 0) : ApiResponse<T>(){
    override fun toString(): String {
        return "ApiErrorResponse(errorMessage='$errorMessage', errorCode=$errorCode)"
    }
}
