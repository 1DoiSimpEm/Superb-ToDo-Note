package com.example.superbtodo.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.superbtodo.data.Task

class TaskDiffUtil(
     val oldList: MutableList<Task>,
     val newList: MutableList<Task>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size


    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = when {
        oldList[oldItemPosition].isDone != newList[newItemPosition].isDone -> {
            false
        }
        oldList[oldItemPosition].title != newList[newItemPosition].title -> {
            false
        }
        oldList[oldItemPosition].date != newList[newItemPosition].date -> {
            false
        }
        oldList[oldItemPosition].timeLeft != newList[newItemPosition].timeLeft -> {
            false
        }
        oldList[oldItemPosition].description != newList[newItemPosition].description -> {
            false
        }
        oldList[oldItemPosition].id != newList[newItemPosition].id -> {
            false
        }
        else -> {
            true
        }
    }


}