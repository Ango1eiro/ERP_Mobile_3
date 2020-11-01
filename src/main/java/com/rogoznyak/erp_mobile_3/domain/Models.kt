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

package com.rogoznyak.erp_mobile_3.domain


/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 */

data class Task(val guid: String,
                 val counterpart: Counterpart,
                 val user: User,
                 val description: String,
                 val date: String) {
}

data class Worksheet(val guid: Long,
                     val counterpart: Counterpart,
                     val description: String,
                     val date: String,
                     val duration: String) {
}

data class Counterpart(val guid: String,
                     val name: String) {
}

data class Contact(val guid: String,
                       val name: String,
                       val counterpart: Counterpart,
                       val phone: String) {
}

data class User(val guid: String,
                   val name: String) {
}

