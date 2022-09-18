package com.example.superbtodo.data.repository

import androidx.lifecycle.LiveData
import com.example.superbtodo.data.Task
import com.example.superbtodo.data.TaskDao


// a repository class abstracts access to multiple data sources
class TaskRepository(private val taskDao: TaskDao) {
    val readAllData: LiveData<MutableList<Task>> = taskDao.readAllData()

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    fun deleteAllNotDoneTasks() = taskDao.deleteAllNotDoneTasks()

    fun deleteAllDoneTasks()=taskDao.deleteAllDoneTasks()

    fun readNotDoneData(): LiveData<MutableList<Task>> = taskDao.readNotDoneData()

    fun readDoneData(): LiveData<MutableList<Task>> = taskDao.readDoneData()

    fun searchDbByTitle(searchQuery: String) : LiveData<MutableList<Task>> = taskDao.searchDbByTitle(searchQuery)

    fun searchIsDoneDbByTitle(searchQuery: String) : LiveData<MutableList<Task>> = taskDao.searchIsDoneDbByTitle(searchQuery)

    fun calendarSearch(searchQuery: String) : LiveData<MutableList<Task>> = taskDao.calendarSearch(searchQuery)

}