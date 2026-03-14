package com.recovery.back.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recovery.back.data.local.AppDatabase
import com.recovery.back.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val appDao = db.appDao()

    private val _userProfile = MutableStateFlow<UserProfileEntity?>(null)
    val userProfile: StateFlow<UserProfileEntity?> = _userProfile.asStateFlow()

    private val _waterCount = MutableStateFlow(0)
    val waterCount = _waterCount.asStateFlow()

    init {
        viewModelScope.launch {
            appDao.getUserProfile().collect {
                _userProfile.value = it
            }
        }
    }

    fun addWater() {
        _waterCount.value += 1
        // In a real app, persistent save to DB here
    }

    fun logWeight(weight: Float) {
        // Logic to log weight to DB
    }
}
