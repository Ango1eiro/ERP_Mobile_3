package com.rogoznyak.erp_mobile_3.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.rogoznyak.erp_mobile_3.network.Todo
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.network.UpdateStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Boolean.FALSE

class SettingsFragmentViewModel : ViewModel() {
    val repository: TodoRepository = TodoRepository()

    val connectionStatusRecieved = FALSE

    val firstTodo = liveData(Dispatchers.IO) {
        try {
            emit(repository.getTodo(2))
        } catch (e: Exception) {
            emit(Todo(0,e.message?:"error",false))
        }
    }

    val updateStatus = liveData(Dispatchers.IO) {
        try {
            emit(UpdateStatus.INPROGRESS)
            emit(repository.updateCatalogues())
        } catch (e: Exception) {
            emit(UpdateStatus.ERROR)
        }
    }

}

