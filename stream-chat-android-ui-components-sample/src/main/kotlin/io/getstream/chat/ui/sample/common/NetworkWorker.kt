package io.getstream.chat.ui.sample.common

import android.util.Log
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.ui.sample.util.extensions.toMap
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
                var errorMessage = ""
                response.body?.let {
                    if (!response.isSuccessful) {
                        if (response.code in 100..499) {
                            val jsonArray = JSONArray(it.string())
                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)
                                if (jsonObject.has("errorMessages")) {
                                    errorMessage = jsonObject.getJSONArray("errorMessages").join("\n").replace("\"", "", false)
                                }
                            }
                        } else {
                            errorMessage = response.message
                        }
                    } else {
                        val jsonObject = JSONObject(it.string())
                        val userResponse = jsonObject.toMap()

                        val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = errorMessage, response = userResponse)
                        callback.invoke(networkResponse)
                        return
                    }
                }
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = errorMessage)
                callback.invoke(networkResponse)
            }
        })
    }

    fun registerUser(username: String, name: String, password: String, phoneNo: String, countryCode: String, streamRole: String = "user", generateOtp: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("name" to name, "username" to username, "password" to password, "phonenumber" to phoneNo, "countrycode" to "+" + countryCode, "generateotp" to generateOtp, "streamrole" to streamRole), url = "registerUser", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                var errorMessage = ""
                response.body?.let {
                    if (!response.isSuccessful) {
                        if (response.code in 100..499) {
                            val jsonArray = JSONArray(it.string())
                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)
                                if (jsonObject.has("errorMessages")) {
                                    errorMessage = jsonObject.getJSONArray("errorMessages").join("\n").replace("\"", "", false)
                                }
                            }
                        } else {
                            errorMessage = response.message
                        }
                    }
                }
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = errorMessage)
                callback.invoke(networkResponse)
            }
        })
    }

    fun resetPassword(username: String, password: String, confirmPassword: String, generateOtp: String, callback: (NetworkResponse) -> Unit)  {
        val request = Endpoint(requestParams = mapOf("username" to username, "newpassword" to password, "confirmpassword" to confirmPassword, "generateotp" to generateOtp), url = "ResetPasswordUser", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                var errorMessage = ""
                response.body?.let {
                    if (!response.isSuccessful) {
                        val jsonArray = JSONArray(it.string())
                        if (jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            if (jsonObject.has("errorMessages")) {
                                errorMessage = jsonObject.getJSONArray("errorMessages").join("\n").replace("\"", "", false)
                            }
                        }
                    }
                }
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = errorMessage, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun deleteAccount(username: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("username" to username), url = "DeactivateUser", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                var errorMessage = ""
                response.body?.let {
                    if (!response.isSuccessful) {
                        if (response.code in 100..499) {
                            val jsonArray = JSONArray(it.string())
                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)
                                if (jsonObject.has("errorMessages")) {
                                    errorMessage = jsonObject.getJSONArray("errorMessages").join("\n")
                                }
                            }
                        } else {
                            errorMessage = response.message
                        }
                    }
                }
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = errorMessage, response = mapOf("" to "", "" to ""))
                callback.invoke(networkResponse)
            }
        })
    }

    fun generateOTP(phoneNo: String, countryCode: String, callback: (NetworkResponse) -> Unit) {
        val request = Endpoint(requestParams = mapOf("phoneNumber" to phoneNo, "countryCode" to "+$countryCode"), url = "generateOtp", httpMethod = HttpMethod.POST).toRequest()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = e.message!!)
                callback.invoke(networkResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                val networkResponse = NetworkResponse(isSuccess = response.isSuccessful, errorMessage = response.message)
                callback.invoke(networkResponse)
            }
        })
    }

    fun uploadFile(imageFile: File, callback: (NetworkResponse) -> Unit) {
        val client = ChatClient.instance()
        val channelClient = client.channel("messaging", "general")

        // Upload an image without detailed progress
        channelClient.sendImage(imageFile).enqueue { result ->
            if (result.isSuccess) {
                // Successful upload, you can now attach this image
                // to a message that you then send to a channel
                Log.d("", "upload success")
                result.data()?.let {
                    val networkResponse = NetworkResponse(isSuccess = true, errorMessage = "", response = mapOf("imageUrl" to it.file))
                    callback.invoke(networkResponse)
                }
            } else {
                Log.d("", "upload failed")
                val networkResponse = NetworkResponse(isSuccess = false, errorMessage = "Upload failed")
                callback.invoke(networkResponse)
            }
        }
    }
}

class Endpoint(val requestParams: Map<String, String>, val url: String, val httpMethod: HttpMethod) {
    fun toRequest(): Request {
        val requestBuilder = Request.Builder()
        requestBuilder.addHeader("Content-Type", "application/json")
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

data class NetworkResponse(val isSuccess: Boolean, val errorMessage: String, val response: Map<String, *>? = null)
