package com.rogoznyak.erp_mobile_3.tasks

import android.app.Application
import androidx.lifecycle.*
import com.rogoznyak.erp_mobile_3.database.AppDao
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel (val database: AppDao, application: Application)
    : AndroidViewModel(application)  {
    private val _navigateToNewTask = MutableLiveData<Boolean>()
    val navigateToNewTask: LiveData<Boolean>
        get() = _navigateToNewTask

    private val _syncIsOn = MutableLiveData<Boolean>()
    val syncIsOn: LiveData<Boolean>
        get() = _syncIsOn
    fun onSyncClicked() {
        _syncIsOn.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val todoList = TodoRepository().sendTasks()
            _syncIsOn.postValue(false)
        }

    }

    fun onFabClicked() {
        _navigateToNewTask.value = true
    }

    fun onNavigatedToTask() {
        _navigateToNewTask.value = false
    }

    val tasksList = database.getTasksListFullData()

    private val _navigateToExistingTask = MutableLiveData<Task>()
    val navigateToExistingTask: LiveData<Task>
        get() = _navigateToExistingTask

    fun setExistingTaskToNavigate(task: Task) {
        _navigateToExistingTask.value = task
    }

    fun doneNavigatingToExistingTask() {
        _navigateToExistingTask.value = null
    }
}