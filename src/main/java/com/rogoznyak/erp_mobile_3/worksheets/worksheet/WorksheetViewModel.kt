package com.rogoznyak.erp_mobile_3.worksheets.worksheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorksheetViewModel : ViewModel() {
    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch

    fun onButtonClicked() {
        _navigateToSearch.value = true
    }

    fun onNavigatedToSearch() {
        _navigateToSearch.value = false
    }

    var counterpartGuid = ""

}