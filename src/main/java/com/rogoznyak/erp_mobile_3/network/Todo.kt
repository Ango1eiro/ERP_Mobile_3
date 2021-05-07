package com.rogoznyak.erp_mobile_3.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.database.*
import com.rogoznyak.erp_mobile_3.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject
import kotlin.text.StringBuilder


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

    @GET("erp_anit/hs/erp_mobile_app/")
    suspend fun getTodoTest(): Todo

    @POST("erp_anit/hs/erp_mobile_app/users")
    suspend fun updateUserList(): List<User>

    @POST("erp_anit/hs/erp_mobile_app/counterparts")
    suspend fun updateCounterpartList(): List<Counterpart>

    @POST("erp_anit/hs/erp_mobile_app/contacts")
    suspend fun updateContactList(): List<NetworkContact>

    @POST("erp_anit/hs/erp_mobile_app/worksheet")
    suspend fun sendWorksheet(@Body networkWorksheet: NetworkWorksheet): Todo

    @POST("erp_anit/hs/erp_mobile_app/task")
    suspend fun sendTask(@Body networkTask: NetworkTask): Todo

}

val webservice by lazy {

    val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val hostInterceptor = Interceptor { chain: Interceptor.Chain ->
        var request = chain.request()
        val oldUrl = request.url
        val segments = oldUrl.pathSegments
        val newUrl = MyCredentials().url

        newUrl?.let {
            val sb = StringBuilder()
            sb.append(newUrl)
            for (segment in segments) sb.append(segment).append("/")
            if (!sb.toString().equals(oldUrl)) {
                request = request.newBuilder()
                    .url(sb.toString())
                    .build()
            }
        }

        chain.proceed(request)
    }

    val client : OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(hostInterceptor)
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

    suspend fun getUserByGuid(guid: String) : User {
        val user: DatabaseUser
        withContext(Dispatchers.IO){
            user = getDatabase(appContext).AppDao.getUserByGuid(guid)
        }
        return user.transform()
    }

    suspend fun getWorksheetByGuid(guid: Long) : Worksheet {
        val worksheet: DatabaseWorksheetFullData
        withContext(Dispatchers.IO){
            worksheet = getDatabase(appContext).AppDao.getWorksheetFullDataByGuid(guid)
        }
        return worksheet.transform()
    }

    suspend fun getTaskByGuid(guid: Long) : Task {
        val task: DatabaseTaskFullData
        withContext(Dispatchers.IO){
            task = getDatabase(appContext).AppDao.getTaskFullDataByGuid(guid)
        }
        return task.transform()
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

    suspend fun sendTask(networkTask: NetworkTask) : Todo {
        return client.sendTask(networkTask)
    }

    suspend fun sendTasks() : List<Todo> {
        val todoList = mutableListOf<Todo>()
        val tasksList = withContext(Dispatchers.IO){getDatabase(appContext).AppDao.getTasksListBlocking()}
        for (task in tasksList) {
            var todo = sendTask(
                NetworkTask(
                    date = task.date,
                    counterpart = task.guidCounterpart,
                    user = task.guidUser,
                    description = task.description
                )
            )
            if (todo.title == "Success") {
                getDatabase(appContext).AppDao.deleteTaskByGuid(task.guid)
            }
            todoList.add(todo)
        }
        return todoList
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

    suspend fun saveTask(databaseTask: DatabaseTask) {
        withContext(Dispatchers.IO){
            getDatabase(appContext).AppDao.insertOrUpdate(databaseTask)
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



