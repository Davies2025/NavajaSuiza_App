package com.example.navajasuiza.data.repository

import com.example.navajasuiza.data.daos.UserDao
import com.example.navajasuiza.data.entities.User

class UserRepository(private val userDao: UserDao) {


    suspend fun registerUser(email: String, fullName: String, sportsActivity: String, password: String) {
        val newUser = User(
            email = email,
            fullName = fullName,
            sportsActivity = sportsActivity,
            password = password
        )
        userDao.insertUser(newUser)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.findByEmailAndPassword(email, password)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }
}

