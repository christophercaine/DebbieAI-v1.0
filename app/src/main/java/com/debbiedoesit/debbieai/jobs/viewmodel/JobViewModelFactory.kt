package com.debbiedoesit.debbieai.jobs.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesit.debbieai.core.database.DebbieDatabase
import com.debbiedoesit.debbieai.jobs.data.repository.JobRepository

class JobViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobViewModel::class.java)) {
            val database = DebbieDatabase.getDatabase(context)
            val repository = JobRepository(database.jobDao())
            return JobViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
