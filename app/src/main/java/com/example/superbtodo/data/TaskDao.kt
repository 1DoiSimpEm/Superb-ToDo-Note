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

    @Query("SELECT * FROM task_table ORDER BY title ASC")
    fun readAllData(): LiveData<MutableList<Task>>


    @Query("DELETE FROM task_table")
    fun deleteAllTasks()

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery ")
    fun searchDbByTitle(searchQuery: String) : LiveData<MutableList<Task>>

    @Query("SELECT * FROM task_table ORDER BY " +
            "CASE WHEN:choice=1 THEN date END ASC," +
            "CASE WHEN:choice=2 THEN title END ASC")
    fun sortAllData(choice : Int ):LiveData<MutableList<Task>>

}
