package com.example.superbtodo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.superbtodo.data.Task
import com.example.superbtodo.data.TaskDao
import com.example.superbtodo.data.TaskDatabase
import kotlinx.coroutines.flow.Flow

// a repository class abstracts access to multiple data sources
class TaskRepository (private val taskDao : TaskDao){
    val readAllData : LiveData<MutableList<Task>> = taskDao.readAllData()

    suspend fun addTask(task : Task){
        taskDao.addTask(task)
    }

    suspend fun deleteTask(task : Task)
    {
        taskDao.deleteTask(task)
    }

    suspend fun updateTask(task:Task)
    {
        taskDao.updateTask(task)
    }


}