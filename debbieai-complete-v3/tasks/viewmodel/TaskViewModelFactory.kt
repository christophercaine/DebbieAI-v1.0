package com.debbiedoesit.debbieai.tasks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesit.debbieai.core.database.DebbieDatabase
import com.debbiedoesit.debbieai.tasks.data.repository.TaskRepository

class TaskViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            val database = DebbieDatabase.getDatabase(context)
            val repository = TaskRepository(database.taskDao())
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
