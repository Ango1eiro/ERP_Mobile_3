package com.rogoznyak.erp_mobile_3.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rogoznyak.erp_mobile_3.database.AppDao
import com.rogoznyak.erp_mobile_3.database.DatabaseCounterpart
import com.rogoznyak.erp_mobile_3.domain.Counterpart

class SearchFragmentViewModel(val database: AppDao, application: Application)
    : AndroidViewModel(application) {

    val counterparts = database.getCounterparts()


    fun applyFilterToCounterparts (text: String?) {
        text?.let {

//            _counterparts.value = database.getCounterpartsWithFilter(text).value

        }
    }


}