package com.example.stranded.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(val repository: Repository): ViewModel() {

    fun startSequence() {
        viewModelScope.launch {
            val startupSaveData = UserSave(1, true, 1, 1)
            repository.updateUserSaveData(startupSaveData)
        }
    }
}