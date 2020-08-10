package com.rogoznyak.erp_mobile_3.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.rogoznyak.erp_mobile_3.network.Todo
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import kotlinx.coroutines.Dispatchers
import java.lang.Boolean.FALSE

class SettingsFragmentViewModel : ViewModel() {
    val repository: TodoRepository = TodoRepository()

    val connectionStatusRecieved = FALSE

    val firstTodo = liveData(Dispatchers.IO) {
        val retrivedTodo = repository.getTodo(2)

        emit(retrivedTodo)
    }
}