package com.example.superbtodo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.superbtodo.data.Task
import com.example.superbtodo.data.TaskDatabase
import com.example.superbtodo.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel is to provide data to the ui and survive configuration changes and it also acts as a communication center between the Repository and the UI
class TaskViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val readAllData: LiveData<MutableList<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = repository.readAllData
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun deleteAllNotDoneTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotDoneTasks()
        }
    }

    fun deleteAllDoneTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllDoneTasks()
        }
    }

    fun searchDbByTitle(searchQuery: String) : LiveData<MutableList<Task>> =  repository.searchDbByTitle(searchQuery)

    fun readNotDoneData(): LiveData<MutableList<Task>> = repository.readNotDoneData()

    fun readDoneData(): LiveData<MutableList<Task>> = repository.readDoneData()

    fun searchIsDoneDbByTitle(searchQuery: String) : LiveData<MutableList<Task>> = repository.searchIsDoneDbByTitle(searchQuery)

    fun calendarSearch(searchQuery: String) : LiveData<MutableList<Task>> = repository.calendarSearch(searchQuery)

}
