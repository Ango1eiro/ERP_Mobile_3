/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rogoznyak.erp_mobile_3.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface AppDao {

    @Query("select * from DatabaseTask")
    fun getTasks(): LiveData<List<DatabaseTask>>

    @Query("select DatabaseTask.guid, DatabaseTask.description, DatabaseTask.date, DatabaseCounterpart.name as counterpart, DatabaseUser.name as user"
            + " from DatabaseTask,DatabaseCounterpart,DatabaseUser"
            + " where DatabaseTask.guidCounterpart = DatabaseCounterpart.guid and DatabaseTask.guidUser = DatabaseUser.guid")
    fun getTasksWithNames(): LiveData<List<DatabaseTaskWithNames>>

    @Query("select * from DatabaseUser")
    fun getUsers(): LiveData<List<DatabaseUser>>

    @Query("select COUNT(*) from DatabaseUser")
    fun getUsersQuantity(): Long

    @Query("select * from DatabaseCounterpart")
    fun getCounterparts(): LiveData<List<DatabaseCounterpart>>

    @Query("select * from DatabaseCounterpart where name LIKE :text")
    fun getCounterpartsWithFilter(text:String): LiveData<List<DatabaseCounterpart>>

    @Query("select * from DatabaseCounterpart where guid = :text")
    fun getCounterpartByGuid(text:String): DatabaseCounterpart

    @Query("select * from DatabaseWorksheet")
    fun getWorksheetsList(): LiveData<List<DatabaseWorksheet>>

    @Query("select * from DatabaseWorksheet")
    fun getWorksheetsListBlocking(): List<DatabaseWorksheet>

    @Query("select DatabaseWorksheet.guid, DatabaseWorksheet.description, DatabaseWorksheet.date, DatabaseWorksheet.duration, DatabaseWorksheet.guidCounterpart, DatabaseCounterpart.name as nameCounterpart"
            +" from DatabaseWorksheet,DatabaseCounterpart"
            +" where DatabaseWorksheet.guidCounterpart = DatabaseCounterpart.guid")
    fun getWorksheetsListFullData(): LiveData<List<DatabaseWorksheetFullData>>

    @Query("select DatabaseWorksheet.guid, DatabaseWorksheet.description, DatabaseWorksheet.date, DatabaseWorksheet.duration, DatabaseWorksheet.guidCounterpart, DatabaseCounterpart.name as nameCounterpart"
            +" from DatabaseWorksheet,DatabaseCounterpart"
            +" where DatabaseWorksheet.guidCounterpart = DatabaseCounterpart.guid and DatabaseWorksheet.guid = :text")
    fun getWorksheetFullDataByGuid(text:Long): DatabaseWorksheetFullData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg task: DatabaseTask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreAction(entity: DatabaseTask): Long

    @Transaction
    fun insertOrUpdate(entity: DatabaseTask){
        if (insertIgnoreAction(entity) == -1L) {
            update(entity)
        }
    }

    @Update
    fun update(entity: DatabaseTask)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIgnoreAction(entity: DatabaseWorksheet): Long

    @Transaction
    fun insertOrUpdate(entity: DatabaseWorksheet){
        val result = insertIgnoreAction(entity)
        if (result == -1L) {
            update(entity)
        }
    }

    @Update
    fun update(entity: DatabaseWorksheet)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreAction(entity: DatabaseCounterpart): Long

    @Transaction
    fun insertOrUpdate(entity: DatabaseCounterpart){
        if (insertIgnoreAction(entity) == -1L) {
            update(entity)
        }
    }

    @Update
    fun update(entity: DatabaseCounterpart)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreAction(entity: DatabaseUser): Long

    @Transaction
    fun insertOrUpdate(entity: DatabaseUser) : Long{
        val result = insertIgnoreAction(entity)
        if (result == -1L) {
            update(entity)
        }
        return result
    }

    @Update
    fun update(entity: DatabaseContact)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreAction(entity: DatabaseContact): Long

    @Transaction
    fun insertOrUpdate(entity: DatabaseContact) : Long{
        val result = insertIgnoreAction(entity)
        if (result == -1L) {
            update(entity)
        }
        return result
    }

    @Update
    fun update(entity: DatabaseUser)

    @Query("DELETE from DatabaseWorksheet WHERE DatabaseWorksheet.guid = :text")
    fun deleteWorksheetByGuid(text: Long)

}

@Database(entities = [DatabaseTask::class,DatabaseWorksheet::class,DatabaseCounterpart::class,DatabaseContact::class,DatabaseUser::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract val AppDao: AppDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    if (!::INSTANCE.isInitialized) synchronized(RoomDatabase::class.java) {
        INSTANCE = Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java,
                "appDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
    return INSTANCE
}