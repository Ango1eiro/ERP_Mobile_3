package com.rogoznyak.erp_mobile_3.network

import android.util.Base64
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.Boolean.FALSE


val URL_test = "http://172.31.255.99:8080/"
val URL = "https://jsonplaceholder.typicode.com/"

data class Todo(
    val id: Int = 0,
    val title: String = "",
    val completed: Boolean = false
)

interface Webservice {
    @GET("/todos/{id}")
    suspend fun getTodo(@Path(value = "id") todoId: Int): Todo

    @GET("anit_erp_test/hs/erp_mobile_app/")
    suspend fun getTodoTest(): Todo
}

val webservice by lazy {
    val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val au = Authenticator

    val client : OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
//        this.addInterceptor(BasicAuthInterceptor("Рогозняк Вячеслав", "12345"))
            .followRedirects(FALSE)
            .followSslRedirects(FALSE)
        this.authenticator(object:Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
//                val credential = Credentials.basic("Рогозняк Вячеслав", "12345")
                val credential = "basic " + Base64.encodeToString(("Рогозняк Вячеслав" + ":" + "12345").toByteArray(),Base64.NO_WRAP)
                if (credential.equals(response.request.header("Authorization"))) {
                    return null; // If we already failed with these credentials, don't retry.
                }

                return response.request.newBuilder().header("Authorization", credential).build()
            }
        })
    }
        .build()

    Retrofit.Builder()
        .baseUrl(URL_test)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .client(client)
        .build()
        .create(Webservice::class.java)
}

class TodoRepository {
    var client: Webservice = webservice

//    suspend fun getTodo(id: Int) = client.getTodo(id)
suspend fun getTodo(id: Int) = client.getTodoTest()
}



