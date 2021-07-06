package com.example.stranded.chatpage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.PromptLine
import com.example.stranded.database.UserSave
import com.example.stranded.notifyObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    val userSave = repository.userSave
    lateinit var sequence: Sequence

    private val _chatDataset = MutableLiveData(mutableListOf<String>())
    val chatDataset: LiveData<MutableList<String>>
        get() = _chatDataset

    private val _consoleDataset = MutableLiveData<MutableList<String>>(mutableListOf())
    val consoleDataset: LiveData<MutableList<String>>
        get() = _consoleDataset

    //TODO implement prompt display
    private val _promptDataset = MutableLiveData<MutableList<PromptLine>>(mutableListOf())
    val promptDataset: LiveData<MutableList<PromptLine>>
        get() = _promptDataset

    init {
        //grabbing the sequence from the repository
        viewModelScope.launch {
            sequence = repository.getSequence(userSave.value?.sequence ?: 1)

            for (scriptLine in sequence.scriptLines) {

                if (scriptLine.consoleLine) {
                    _consoleDataset.value!!.add(scriptLine.line)
                    _consoleDataset.notifyObserver()
                }
                else {
                    _chatDataset.value!!.add(scriptLine.line)
                    _chatDataset.notifyObserver()
                }
            }
        }
    }
}