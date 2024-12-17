package eu.tutorials.courseapplication.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eu.tutorials.courseapplication.MainViewModel

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(context.applicationContext as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}