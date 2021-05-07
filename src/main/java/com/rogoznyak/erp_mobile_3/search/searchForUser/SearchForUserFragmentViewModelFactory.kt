package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rogoznyak.erp_mobile_3.database.AppDao
import com.rogoznyak.erp_mobile_3.search.SearchFragmentViewModel

class SearchForUserFragmentViewModelFactory(
    private val dataSource: AppDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchForUserFragmentViewModel::class.java)) {
            return SearchForUserFragmentViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}