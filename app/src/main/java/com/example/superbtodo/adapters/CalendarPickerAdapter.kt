package com.example.superbtodo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.fragments.calendar.CalendarFragmentDirections
import com.example.superbtodo.utils.TaskDiffUtil
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class CalendarPickerAdapter : RecyclerView.Adapter<CalendarPickerAdapter.CalendarViewHolder>() {
    private var tasks = mutableListOf<Task>()
    private val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.titleTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val taskLayout = itemView.findViewById(R.id.taskLayout) as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_task_layout, parent, false)
        return CalendarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        initGadgets(holder,position)
        navigate(holder,position)
    }

    private fun navigate(holder: CalendarPickerAdapter.CalendarViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.taskLayout.setOnClickListener {
                val action = CalendarFragmentDirections.actionCalendarFragmentToUpdateTaskDialogFragment(currentItem)
                holder.itemView.findNavController().navigate(action)

        }
    }

    private fun initGadgets(holder : CalendarViewHolder,position: Int) {
        val currentItem = tasks[position]
        holder.titleTextView.text = currentItem.title
        holder.timeTextView.text =  timeFormat.format(hourly.parse(currentItem.date) as Date)
    }

    override fun getItemCount(): Int = tasks.size

    fun setData(newTasks: MutableList<Task>) {
        val diffUtil = TaskDiffUtil(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

}