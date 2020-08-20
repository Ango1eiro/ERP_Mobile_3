package com.rogoznyak.erp_mobile_3.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rogoznyak.erp_mobile_3.database.AppDao

class SearchFragmentViewModel(val database: AppDao, application: Application)
    : AndroidViewModel(application) {

    val counterparts = database.getCounterparts()


}