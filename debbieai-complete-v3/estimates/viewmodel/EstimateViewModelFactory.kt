package com.debbiedoesit.debbieai.estimates.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesit.debbieai.core.database.DebbieDatabase
import com.debbiedoesit.debbieai.estimates.data.repository.EstimateRepository

class EstimateViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstimateViewModel::class.java)) {
            val database = DebbieDatabase.getDatabase(context)
            val repository = EstimateRepository(
                estimateDao = database.estimateDao(),
                lineItemDao = database.lineItemDao(),
                materialDao = database.materialDao(),
                templateDao = database.templateDao()
            )
            return EstimateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}