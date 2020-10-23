package com.rogoznyak.erp_mobile_3.worksheets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rogoznyak.erp_mobile_3.database.getDatabase

class WorksheetsViewModel : ViewModel() {
    private val _navigateToNewWorksheet = MutableLiveData<Boolean>()
    val navigateToNewWorksheet: LiveData<Boolean>
        get() = _navigateToNewWorksheet

    fun onFabClicked() {
        _navigateToNewWorksheet.value = true
    }

    fun onNavigatedToWorksheet() {
        _navigateToNewWorksheet.value = false
    }

}