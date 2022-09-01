package com.example.superbtodo.data
// TaskDao was built for the purpose containing the methods used for accessing the database
// TaskDao ( data access object ) is a interface cuz it provides methods that the rest of the app uses to interact with data in the table
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    // means it will be just ignore if there is a new exactly same task then we're gonna just ignore it
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task :Task)

    @Delete
    suspend fun deleteTask(task : Task)

    @Update
    suspend fun  updateTask(task : Task)

    @Query("SELECT * FROM task_table order by timeLeft ASC")
    fun readAllData(): LiveData<MutableList<Task>>


}