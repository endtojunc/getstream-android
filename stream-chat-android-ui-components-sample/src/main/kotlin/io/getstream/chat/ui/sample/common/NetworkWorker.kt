package io.getstream.chat.ui.sample.common

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class NetworkWorker {
    fun authenticateUser(username: String, password: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("username" to username, "password" to password), url = "loginUser", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body.toString())
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun registerUser(username: String, name: String, password: String, phoneNo: String, countryCode: String, streamRole: String = "user", generateOtp: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("username" to username, "password" to password, "phoneNo" to phoneNo, "countryCode" to countryCode, "generateOtp" to generateOtp, "streamRole" to streamRole), url = "registerUser", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body.toString())
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun resetPassword(username: String, password: String, confirmPassword: String, generateOtp: String, callback: (NetworkResponse) -> Unit)  {
        val request = Endpoint(requestParams = mapOf("username" to ""), url = "", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body.toString())
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun deleteAccount(userId: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("username" to ""), url = "", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body.toString())
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun generateOTP(phoneNo: String, countryCode: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("phoneNO" to phoneNo, "countryCode" to countryCode), url = "generateUer", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body.toString())
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun uploadFile(fileName: String): String {
        return ""
    }
}

class Endpoint(val requestParams: Map<String, String>, val url: String, val httpMethod: HttpMethod) {
    fun toRequest(): Request {
        val requestBuilder = Request.Builder()
        requestBuilder.addHeader("x-api-key", "d9af4f56-5402-4283-9b92-09f321712fb4")
        requestBuilder.url(URL("https://test-chattingapp-api.directmessenger.info/api/MobileAuth/$url"))
        if (httpMethod == HttpMethod.POST) {
            requestBuilder.post(JSONObject(requestParams).toString().toRequestBody(MEDIA_JSON))
        }
        return requestBuilder.build()
    }

    companion object {
        val MEDIA_JSON = "application/json".toMediaType()
    }
}

enum class HttpMethod {
    GET, POST
}

data class NetworkResponse(val isSuccess: Boolean, val errorMessage: String, val response: Map<String, String>? = null)