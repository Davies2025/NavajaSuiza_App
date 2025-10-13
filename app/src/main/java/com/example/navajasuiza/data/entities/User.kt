package com.example.navajasuiza.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "sports_activity")
    val sportsActivity: String,

    @ColumnInfo(name = "password")
    val password: String
)