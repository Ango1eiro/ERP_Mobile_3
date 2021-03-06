package com.rogoznyak.erp_mobile_3.database

import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.domain.Contact
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.User
import com.rogoznyak.erp_mobile_3.network.NetworkContact

fun Task.transform() = DatabaseTask(
    guid = this.guid,
    guidCounterpart = this.counterpart.guid,
    guidUser = this.user.guid,
    description = this.description,
    date = this.date
)

fun DatabaseTask.transform() = Task(
    guid = this.guid,
    counterpart = MyCustomApplication.gson.fromJson(this.guidCounterpart,Counterpart::class.java),
    user = MyCustomApplication.gson.fromJson(this.guidUser,User::class.java),
    description = this.description,
    date = this.date
)

fun Counterpart.transform() = DatabaseCounterpart(
     guid = this.guid,
     name = this.name)

fun DatabaseCounterpart.transform() = Counterpart(
    guid = this.guid,
    name = this.name)

fun User.transform() = DatabaseUser(
    guid = this.guid,
    name = this.name)

fun List<User>.transformUser() : List<DatabaseUser> {
    return map{
        DatabaseUser(
            guid = it.guid,
            name = it.name
        )
    }
}

fun List<Counterpart>.transformCounterpart() : List<DatabaseCounterpart> {
    return map{
        DatabaseCounterpart(
            guid = it.guid,
            name = it.name
        )
    }
}

fun List<DatabaseCounterpart>.transformFromDatabaseCounterpartToCounterpart() : List<Counterpart> {
    return map{
        Counterpart(
            guid = it.guid,
            name = it.name
        )
    }
}

fun List<NetworkContact>.networkContactToDatabaseContact() : List<DatabaseContact> {
    return map{
        DatabaseContact(
            guid = it.guid,
            name = it.name,
            guidCounterpart = it.guidCounterpart,
            phone = it.phone
        )
    }
}

