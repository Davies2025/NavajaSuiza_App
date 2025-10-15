package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navajasuiza.data.AppDatabase
import com.example.navajasuiza.data.repository.UserRepository


object ViewModelFactoryProvider {

    private var repository: UserRepository? = null


    private fun provideUserRepository(application: Application): UserRepository {
        if (repository == null) {
            val db = AppDatabase.getInstance(application)
            repository = UserRepository(db.userDao())
        }
        return repository!!
    }


    fun getLoginViewModelFactory(application: Application): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    return LoginViewModel(provideUserRepository(application)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

