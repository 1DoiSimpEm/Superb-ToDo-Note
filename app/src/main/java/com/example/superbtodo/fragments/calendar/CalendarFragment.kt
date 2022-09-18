package com.example.superbtodo.fragments.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.applandeo.materialcalendarview.EventDay
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentCalendarBinding
import com.example.superbtodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)
        setupEvents()
    }

    private fun setupEvents() {
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        mTaskViewModel.readNotDoneData().observe(viewLifecycleOwner) { tasks ->
            binding.calendarView.setEvents(getHighLightedEvents(tasks))
        }
    }

    private fun getHighLightedEvents(tasks: MutableList<Task>): MutableList<EventDay> {
        val events = mutableListOf<EventDay>()
        for (task in tasks) {
            val calendar = Calendar.getInstance()
            val items1: ArrayList<String> = dateFormat.format(hourly.parse(task.date) as Date).toString().split(".") as ArrayList<String>
            Log.d("parse", "$items1!![0] $items1[1]")
            val dd: String = items1[0]
            val month: String = items1[1]
            val year: String = items1[2]

            calendar[Calendar.DAY_OF_MONTH] = dd.toInt()
            calendar[Calendar.MONTH] = month.toInt() - 1
            calendar[Calendar.YEAR] = year.toInt()
            events.add(EventDay(calendar, R.drawable.dot))
        }
        return events

    }


}