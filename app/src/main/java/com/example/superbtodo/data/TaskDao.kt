package com.example.superbtodo.data
// TaskDao was built for the purpose containing the methods used for accessing the database
// TaskDao ( data access object ) is a interface cuz it provides methods that the rest of the app uses to interact with data in the table
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    // means it will be just ignore if there is a new exactly same task then we're gonna just ignore it
    @Insert
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM task_table")
    fun readAllData(): LiveData<MutableList<Task>>

    @Query("SELECT * FROM task_table WHERE isDone=0" )
    fun readNotDoneData(): LiveData<MutableList<Task>>

    @Query("SELECT * FROM task_table WHERE isDone=1" )
    fun readDoneData(): LiveData<MutableList<Task>>

    @Query("DELETE FROM task_table WHERE isDone =0")
    fun deleteAllNotDoneTasks()

    @Query("DELETE FROM task_table WHERE isDone =1")
    fun deleteAllDoneTasks()

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery ")
    fun searchDbByTitle(searchQuery: String) : LiveData<MutableList<Task>>


}
