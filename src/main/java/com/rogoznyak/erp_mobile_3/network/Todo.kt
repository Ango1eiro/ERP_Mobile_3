package com.rogoznyak.erp_mobile_3.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.widget.Toast
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.database.*
import com.rogoznyak.erp_mobile_3.domain.Contact
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.User
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("anit_erp_test/hs/erp_mobile_app/users")
    suspend fun updateUserList(): List<User>

    @POST("anit_erp_test/hs/erp_mobile_app/counterparts")
    suspend fun updateCounterpartList(): List<Counterpart>

    @POST("anit_erp_test/hs/erp_mobile_app/contacts")
    suspend fun updateContactList(): List<NetworkContact>

    @POST("anit_erp_test/hs/erp_mobile_app/worksheet")
    suspend fun sendWorksheet(@Body networkWorksheet: NetworkWorksheet): Todo

}

val webservice by lazy {
    val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val client : OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
        this.authenticator(object:Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
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
        .baseUrl(MyCredentials().url)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .client(client)
        .build()
        .create(Webservice::class.java)
}

class TodoRepository {

    @Inject
    lateinit var appContext: Context
    var client: Webservice = webservice

    init {
        MyCustomApplication.mAppComponent.inject(this)
    }

    suspend fun getCounterpartByGuid(guid: String) : Counterpart {
        val counterpart: DatabaseCounterpart
        withContext(Dispatchers.IO){
            counterpart = getDatabase(appContext).AppDao.getCounterpartByGuid(guid)
        }
        return counterpart.transform()
    }

    suspend fun getWorksheetByGuid(guid: Long) : Worksheet {
        val worksheet: DatabaseWorksheetFullData
        withContext(Dispatchers.IO){
            worksheet = getDatabase(appContext).AppDao.getWorksheetFullDataByGuid(guid)
        }
        return worksheet.transform()
    }

    suspend fun getTodo(id: Int) = client.getTodoTest()

    suspend fun updateUserList(){
        withContext(Dispatchers.IO){
            val userList = client.updateUserList()
            for (user in userList.transformUser()){
            getDatabase(appContext).AppDao.insertOrUpdate(user)

            }
        }
    }

    suspend fun updateCounterpartList(){
        withContext(Dispatchers.IO){
            val counterpartList = client.updateCounterpartList()
            for (counterpart in counterpartList.transformCounterpart()){
                getDatabase(appContext).AppDao.insertOrUpdate(counterpart)

            }
        }
    }

    suspend fun updateContactList(){
        withContext(Dispatchers.IO){
            val contactList = client.updateContactList()
            for (contact in contactList.networkContactToDatabaseContact()){
                getDatabase(appContext).AppDao.insertOrUpdate(contact)

            }
        }
    }

    suspend fun updateCatalogues() : UpdateStatus {
        updateUserList()
        updateCounterpartList()
        updateContactList()
        return UpdateStatus.DONE
    }

    suspend fun sendWorksheet(networkWorksheet: NetworkWorksheet) : Todo {
        return client.sendWorksheet(networkWorksheet)
    }

    suspend fun sendWorksheets() : List<Todo> {
        val todoList = mutableListOf<Todo>()
        val worksheetsList = withContext(Dispatchers.IO){getDatabase(appContext).AppDao.getWorksheetsListBlocking()}
            for (worksheet in worksheetsList) {
                var todo = sendWorksheet(
                    NetworkWorksheet(
                        worksheet.date,
                        worksheet.guidCounterpart,
                        worksheet.duration,
                        worksheet.description
                    )
                )
                if (todo.title == "Success") {
                    getDatabase(appContext).AppDao.deleteWorksheetByGuid(worksheet.guid)
                }
                todoList.add(todo)
            }
        return todoList
    }

    suspend fun saveWorksheet(databaseWorksheet: DatabaseWorksheet) {
        withContext(Dispatchers.IO){
            getDatabase(appContext).AppDao.insertOrUpdate(databaseWorksheet)
        }
    }


}

enum class UpdateStatus {
    INPROGRESS,DONE,ERROR
}

class MyCredentials {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        MyCustomApplication.mAppComponent.inject(this)
    }

    val credential = "basic " + Base64.encodeToString((sharedPreferences.getString("login","login") + ":" + sharedPreferences.getString("password","password")).toByteArray(),Base64.NO_WRAP)
    val url = sharedPreferences.getString("host","172.31.255.0")
}



