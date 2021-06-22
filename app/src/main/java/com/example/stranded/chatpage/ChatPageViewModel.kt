package com.example.stranded.chatpage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatPageViewModel @Inject constructor (val repository: Repository): ViewModel() {

    val userSave = repository.userSave

    fun initializeDatabase() {
        viewModelScope.launch {
            val testSaveData = UserSave(1, true, 1, 0)
            repository.updateUserSaveData(testSaveData)
        }
    }
}