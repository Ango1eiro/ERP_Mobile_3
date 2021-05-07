package com.rogoznyak.erp_mobile_3.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _connectionStatusNeedsUpdate = MutableLiveData<Boolean>()
    val connectionStatusNeedsUpdate: LiveData<Boolean>
        get() = _connectionStatusNeedsUpdate

    fun setConnectionStatusNeedsUpdate(Status: Boolean) {
        _connectionStatusNeedsUpdate.value = Status
    }

    suspend fun getConnectionStatus() = repository.getTodo(2).title

    private val _updateStatusNeedsUpdate = MutableLiveData<Boolean>()
    val updateStatusNeedsUpdate: LiveData<Boolean>
        get() = _updateStatusNeedsUpdate

    fun setUpdateStatusNeedsUpdate(Status: Boolean) {
        _updateStatusNeedsUpdate.value = Status
    }

    suspend fun getUpdateStatus() = repository.updateCatalogues()


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

