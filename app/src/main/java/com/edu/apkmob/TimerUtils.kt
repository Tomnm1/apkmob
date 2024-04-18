package com.edu.apkmob

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null
    private val _savedTimes = MutableStateFlow<List<Long>>(emptyList())
    val savedTimes = _savedTimes.asStateFlow()

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timer.value++
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun stopTimer() {
        _timer.value = 0
        timerJob?.cancel()
    }

    fun saveTimer() {
        val currentTimerValue = _timer.value
        val currentSavedTimes = _savedTimes.value.toMutableList()
        currentSavedTimes.add(currentTimerValue)
        _savedTimes.value = currentSavedTimes.toList()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}