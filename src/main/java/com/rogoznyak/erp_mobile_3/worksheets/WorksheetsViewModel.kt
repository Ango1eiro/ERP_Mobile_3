package com.rogoznyak.erp_mobile_3.worksheets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WorksheetsViewModel : ViewModel() {
    private val _navigateToWorksheet = MutableLiveData<Boolean>()
    val navigateToWorksheet: LiveData<Boolean>
        get() = _navigateToWorksheet

    fun onFabClicked() {
        _navigateToWorksheet.value = true
    }

    fun onNavigatedToWorksheet() {
        _navigateToWorksheet.value = false
    }
}