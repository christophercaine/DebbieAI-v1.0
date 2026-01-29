package com.debbiedoesit.debbieai.photos.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesit.debbieai.core.database.DebbieDatabase
import com.debbiedoesit.debbieai.photos.data.repository.PhotoRepository

class PhotoViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            val database = DebbieDatabase.getDatabase(context)
            
            val repository = PhotoRepository(
                photoDao = database.photoDao(),
                albumDao = database.albumDao(),
                context = context
            )
            
            return PhotoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
