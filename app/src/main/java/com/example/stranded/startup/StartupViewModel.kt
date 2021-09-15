package com.example.stranded.startup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    val userSave = repository.userSave

    //TODO delete this method completely when the app is finished
    fun startSequence() {
        viewModelScope.launch {
            delay(1000)
            Log.i("bruh", "coroutine startup userSave = ${userSave.value}")

            val newUserSave = UserSave(0, true, userSave.value?.sequence ?: 1, 1)

            repository.updateUserSaveData(newUserSave)
        }
    }
}