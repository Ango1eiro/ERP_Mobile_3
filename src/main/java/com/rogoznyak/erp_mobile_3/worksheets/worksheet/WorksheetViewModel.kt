package com.rogoznyak.erp_mobile_3.worksheets.worksheet

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.network.UpdateStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class WorksheetViewModel : ViewModel() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        MyCustomApplication.mAppComponent.inject(this)
    }

    private val _worksheetData = MutableLiveData<Worksheet>()
    val worksheetData: LiveData<Worksheet>
        get() = _worksheetData

    fun setWorksheetData(guid: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _worksheetData.postValue(TodoRepository().getWorksheetByGuid(guid))
        }
    }

    // Navigation
    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch

    private val _worksheetSent = MutableLiveData<Boolean>()
    val worksheetSent: LiveData<Boolean>
        get() = _worksheetSent

    private val _worksheetStatus = MutableLiveData<String>()
    val worksheetStatus: LiveData<String>
        get() = _worksheetStatus

    private val _worksheetSave = MutableLiveData<Boolean>()
    val worksheetSave: LiveData<Boolean>
        get() = _worksheetSave

    fun setWorksheetStatus(text: String) {
        _worksheetStatus.postValue(text)
    }

    fun onButtonClicked() {
        _navigateToSearch.value = true
    }

    fun onNavigatedToSearch() {
        _navigateToSearch.value = false
    }

    // Counterpart
    private var _counterpart = MutableLiveData<Counterpart>()
    val counterpart : LiveData<Counterpart>
        get() = _counterpart

    fun setCounterpartByGuid(guid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _counterpart.postValue(TodoRepository().getCounterpartByGuid(guid))
        }
    }

    // Date
    private var _date = SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Date())
    val date : String
        get() = _date

    fun FABclicked() {
        if (sharedPreferences.getBoolean("onlineMode",false)) {
            _worksheetSent.value = true
        } else {
            _worksheetSave.value = true
        }
    }

    fun worksheetSent() {
        _worksheetSent.postValue(false)
    }

}