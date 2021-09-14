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
            //TODO this is very bad cuz it will always start sequence one every time
            val newUserSave = UserSave(0, true, 1, 1)

            repository.updateUserSaveData(newUserSave)
        }
    }
}