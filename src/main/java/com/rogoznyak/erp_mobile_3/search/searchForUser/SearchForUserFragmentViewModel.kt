package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rogoznyak.erp_mobile_3.database.AppDao
import com.rogoznyak.erp_mobile_3.database.DatabaseUser
import com.rogoznyak.erp_mobile_3.database.transform
import com.rogoznyak.erp_mobile_3.domain.User
import kotlinx.coroutines.*

class SearchForUserFragmentViewModel(val database: AppDao, application: Application)
    : AndroidViewModel(application) {

    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)

    private var searchString: String = ""
    private var userList = listOf<User>()

    fun getUserList() = userList

    fun setSearchString(str: String?) {
        str?.let { searchString = it }
    }

    suspend fun updateUsersWithFilter() {
        ioScope.launch {
            if (searchString.isEmpty())
                userList = database.getRawUsers().map { it.transform() }
            else
                userList = database.getRawUsersWithFilter(searchString).map { it.transform() }
        }.join()
    }

}