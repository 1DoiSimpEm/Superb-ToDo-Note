package com.example.superbtodo.fragments.calendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.example.superbtodo.R
import com.example.superbtodo.adapters.CalendarPickerAdapter
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentCalendarBinding
import com.example.superbtodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var adapter: CalendarPickerAdapter
    private lateinit var selectedDate : Calendar
    private val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)
        initAdapter()
        setupEvents()
        pickDate()
    }

    private fun pickDate() {
        selectedDate = binding.calendarView.firstSelectedDate
        binding.calendarView.setOnDayClickListener{ eventDay ->
            selectedDate= eventDay.calendar
            val searchQuery = dateFormat.format(selectedDate.timeInMillis).toString()
            Toast.makeText(context,searchQuery,Toast.LENGTH_LONG).show()
            mTaskViewModel.calendarSearch("%$searchQuery%").observe(viewLifecycleOwner)
            { tasks ->
                tasks.let{
                    adapter.setData(it)
                }
            }
        }

    }

    private fun initAdapter() {
        adapter = CalendarPickerAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
            val date: ArrayList<String> =
                dateFormat.format(hourly.parse(task.date) as Date).toString()
                    .split(".") as ArrayList<String>
            val dd: String = date[0]
            val month: String = date[1]
            val year: String = date[2]
            calendar[Calendar.DAY_OF_MONTH] = dd.toInt()
            calendar[Calendar.MONTH] = month.toInt() - 1
            calendar[Calendar.YEAR] = year.toInt()
            events.add(EventDay(calendar, R.drawable.ic_baseline_access_alarm_24))
        }
        return events

    }


}