package com.example.superbtodo.repository

import androidx.lifecycle.LiveData
import com.example.superbtodo.data.Task
import com.example.superbtodo.data.TaskDao

// a repository class abstracts access to multiple data sources
class TaskRepository (private val taskDao : TaskDao){
    val readAllData : LiveData<List<Task>> = taskDao.readAllData()


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