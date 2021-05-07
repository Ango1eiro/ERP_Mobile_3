package com.rogoznyak.erp_mobile_3.worksheets

import android.app.Application
import androidx.lifecycle.*
import com.rogoznyak.erp_mobile_3.database.AppDao
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorksheetsViewModel(val database: AppDao, application: Application)
    : AndroidViewModel(application) {
    private val _navigateToNewWorksheet = MutableLiveData<Boolean>()
    val navigateToNewWorksheet: LiveData<Boolean>
        get() = _navigateToNewWorksheet

    private var _msg =""
    fun getMsg() = _msg

    private val _syncIsOn = MutableLiveData<Boolean>()
    val syncIsOn: LiveData<Boolean>
        get() = _syncIsOn
    fun onSyncClicked() {
        _syncIsOn.value = true
        viewModelScope.launch(Dispatchers.IO) {
            _msg = ""
            try {
                val todoList = TodoRepository().sendWorksheets()
                _msg = "Success"
            } catch (t: Throwable) {
                _msg = t.localizedMessage
            } finally {
                _syncIsOn.postValue(false)
            }
        }

    }

    fun onFabClicked() {
        _navigateToNewWorksheet.value = true
    }

    fun onNavigatedToWorksheet() {
        _navigateToNewWorksheet.value = false
    }

    val worksheetsList = database.getWorksheetsListFullData()

    private val _navigateToExistingWorksheet = MutableLiveData<Worksheet>()
    val navigateToExistingWorksheet: LiveData<Worksheet>
        get() = _navigateToExistingWorksheet

    fun setExistingWorksheetToNavigate(worksheet: Worksheet) {
        _navigateToExistingWorksheet.value = worksheet
    }

    fun doneNavigatingToExistingWorksheet() {
        _navigateToExistingWorksheet.value = null
    }

}