package com.example.stranded.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StartupViewModel is shared by NoPowerFragment and PowerOnFragment.
 *
 * It's only real job is to provide the UserSave in a hot flow format or a traditional async format.
 */
@HiltViewModel
class StartupViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _userSaveFlow = MutableStateFlow<UserSave?>(null)
    val userSaveFlow: StateFlow<UserSave?> = _userSaveFlow

    init {
        viewModelScope.launch {
            repository.userSaveFlow.stateIn(this).collect{ userSave ->
                _userSaveFlow.emit(userSave)
            }
        }
    }

    suspend fun getUserSave(): UserSave = repository.getUserSave()
}