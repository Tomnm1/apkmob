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

    private var isRunning = false
    private var pauseOffset = 0L

    fun startTimer() {
        if (!isRunning) {
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                val startTime = System.currentTimeMillis() - pauseOffset
                while (true) {
                    _timer.value = (System.currentTimeMillis() - startTime) / 1000
                    delay(1000)
                }
            }
            isRunning = true
        }
    }

    fun pauseTimer() {
        if (isRunning) {
            timerJob?.cancel()
            pauseOffset = _timer.value * 1000
            isRunning = false
        }
    }

    fun stopTimer() {
        _timer.value = 0
        pauseOffset = 0
        timerJob?.cancel()
        isRunning = false
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
