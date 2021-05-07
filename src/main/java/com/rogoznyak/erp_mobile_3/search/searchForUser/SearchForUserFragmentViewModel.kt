package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rogoznyak.erp_mobile_3.database.AppDao

class SearchForUserFragmentViewModel(val database: AppDao, application: Application)
    : AndroidViewModel(application) {
    val users = database.getUsers()
    }