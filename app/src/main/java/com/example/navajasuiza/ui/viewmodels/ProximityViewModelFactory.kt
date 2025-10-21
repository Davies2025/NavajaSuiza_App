package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProximityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProximityViewModel::class.java)) {
            return ProximityViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

