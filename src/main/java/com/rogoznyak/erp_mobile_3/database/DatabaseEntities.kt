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

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.User
import com.rogoznyak.erp_mobile_3.domain.Worksheet

@Entity(foreignKeys = [
    ForeignKey(
        entity = DatabaseCounterpart::class,
        parentColumns = arrayOf("guid"),
        childColumns = arrayOf("guidCounterpart")
    ),
    ForeignKey(
        entity = DatabaseUser::class,
        parentColumns = arrayOf("guid"),
        childColumns = arrayOf("guidUser")
    )
])
data class DatabaseTask constructor(
    @PrimaryKey
    val guid: String,
    val guidCounterpart: String,
    val guidUser: String,
    val description: String,
    val date: String)

data class DatabaseTaskWithNames constructor(
    val guid: String,
    val counterpart: String,
    val user: String,
    val description: String,
    val date: String)


@Entity(foreignKeys = [
    ForeignKey(
        entity = DatabaseCounterpart::class,
        parentColumns = arrayOf("guid"),
        childColumns = arrayOf("guidCounterpart")
    )
])
data class DatabaseWorksheet(
    @PrimaryKey(autoGenerate = true)
    val guid: Long,
    val guidCounterpart: String,
    val description: String,
    val date: String,
    val duration: String)

data class DatabaseWorksheetFullData(
    val guid: Long,
    val guidCounterpart: String,
    val nameCounterpart: String,
    val description: String,
    val date: String,
    val duration: String)

@Entity
data class DatabaseCounterpart(
    @PrimaryKey
    val guid: String,
    val name: String) {
}

@Entity(foreignKeys = [
    ForeignKey(
        entity = DatabaseCounterpart::class,
        parentColumns = arrayOf("guid"),
        childColumns = arrayOf("guidCounterpart")
    )
])
data class DatabaseContact(
    @PrimaryKey
    val guid: String,
    val name: String,
    val guidCounterpart: String,
    val phone: String) {
}

@Entity
data class DatabaseUser(
    @PrimaryKey
    val guid: String,
    val name: String) {
}