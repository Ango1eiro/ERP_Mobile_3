package com.rogoznyak.erp_mobile_3.worksheets

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rogoznyak.erp_mobile_3.database.AppDao

class WorksheetsViewModelFactory(
    private val dataSource: AppDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorksheetsViewModel::class.java)) {
            return WorksheetsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}