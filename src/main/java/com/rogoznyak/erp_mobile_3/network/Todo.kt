package com.rogoznyak.erp_mobile_3.network

import android.content.SharedPreferences
import android.util.Base64
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject


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

    val client : OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
//            .followRedirects(FALSE)
//            .followSslRedirects(FALSE)
        this.authenticator(object:Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
//                val credential = "basic " + Base64.encodeToString(("Рогозняк Вячеслав" + ":" + "12345").toByteArray(),Base64.NO_WRAP)
                val credential = MyCredentials().credential
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

class MyCredentials {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        MyCustomApplication.mAppComponent.inject(this)
    }

    val credential = "basic " + Base64.encodeToString((sharedPreferences.getString("login","") + ":" + sharedPreferences.getString("password","")).toByteArray(),Base64.NO_WRAP)
}



