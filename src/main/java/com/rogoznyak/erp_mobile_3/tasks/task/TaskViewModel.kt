package com.rogoznyak.erp_mobile_3.tasks.task

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.User
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.network.UpdateStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class TaskViewModel : ViewModel() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        MyCustomApplication.mAppComponent.inject(this)
    }

    private val _taskData = MutableLiveData<Task>()
    val taskData: LiveData<Task>
        get() = _taskData

    fun setTaskData(guid: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _taskData.postValue(TodoRepository().getTaskByGuid(guid))
        }
    }

    // Navigation
    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch

    private val _navigateToSearchU = MutableLiveData<Boolean>()
    val navigateToSearchU: LiveData<Boolean>
        get() = _navigateToSearchU

    private val _taskSent = MutableLiveData<Boolean>()
    val taskSent: LiveData<Boolean>
        get() = _taskSent

    private val _taskStatus = MutableLiveData<String>()
    val taskStatus: LiveData<String>
        get() = _taskStatus

    private val _taskSave = MutableLiveData<Boolean>()
    val taskSave: LiveData<Boolean>
        get() = _taskSave

    fun setTaskStatus(text: String) {
        _taskStatus.postValue(text)
    }

    fun onButtonClicked(arg: Int) {
        if (arg == 1)
            _navigateToSearch.value = true
        else
            _navigateToSearchU.value = true
    }

    fun onNavigatedToSearch() {
        _navigateToSearch.value = false
    }

    fun onNavigatedToSearchU() {
        _navigateToSearchU.value = false
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

    private var _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

    fun setUserByGuid(guid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(TodoRepository().getUserByGuid(guid))
        }
    }

    // Date
    private var _date = SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Date())
    val date : String
        get() = _date

    fun FABclicked() {
        if (sharedPreferences.getBoolean("onlineMode",false)) {
            _taskSent.value = true
        } else {
            _taskSave.value = true
        }
    }

    fun taskSent() {
        _taskSent.postValue(false)
    }

}