package com.rogoznyak.erp_mobile_3

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.rogoznyak.erp_mobile_3.database.AppDatabase
import com.rogoznyak.erp_mobile_3.database.transform
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.User
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


//@RunWith(AndroidJUnit4::class)
//class DatabaseTest {
//    @Rule
//    @JvmField public val rule = InstantTaskExecutorRule()
//
//    private fun <T> LiveData<T>.blockingObserve(): T? {
//        var value: T? = null
//        val latch = CountDownLatch(1)
//
//        val observer = androidx.lifecycle.Observer<T> { t ->
//            value = t
//            latch.countDown()
//        }
//
//        observeForever(observer)
//
//        latch.await(2, TimeUnit.SECONDS)
//        return value
//    }
//
//    lateinit var appDatabase: AppDatabase
//    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//
//    @Before
//    fun init() {
//        appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//    }
//
//
//    @Test
//    fun test() {
//
//        val listTasks = (0..100).map {
//            val user = User(UUID.randomUUID().toString() + "-$it","User" + "-$it");
//            val counterpart = Counterpart(UUID.randomUUID().toString() + "-$it","Counterpart" + "-$it");
//            val userResult = appDatabase.AppDao.insertOrUpdate(user.transform())
//            appDatabase.AppDao.insertOrUpdate(counterpart.transform())
//            Task(
//                guid = UUID.randomUUID().toString() + "-$it",
//                counterpart = counterpart,
//                user = user,
//                description = "Description -$it",
//                date = "Date -$it"
//            )
//        }
//
//        listTasks.forEach {
//            appDatabase.AppDao.insertOrUpdate(it.transform())
//        }
//
//        val listUser = appDatabase.AppDao.getUsers().blockingObserve()
//        assert(listUser!!.isNotEmpty())
//        Log.v("TEST",listUser.size.toString())
//
//        val listConterpart = appDatabase.AppDao.getUsers().blockingObserve()
//        assert(listConterpart!!.isNotEmpty())
//        Log.v("TEST",listConterpart.size.toString())
//
//        val listFromDB = appDatabase.AppDao.getTasks().blockingObserve()
//        assert(listFromDB!!.isNotEmpty())
//        Log.v("TEST",listFromDB.size.toString())
//
//        val listTasksWithNames = appDatabase.AppDao.getTasksWithNames().blockingObserve()
//        assert(listTasksWithNames!!.isNotEmpty())
//    }
//
//    @After
//    fun closeDb() {
//        appDatabase.close()
//    }
//
//
//}